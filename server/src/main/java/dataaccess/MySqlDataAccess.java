package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(256) NOT NULL,
            password VARCHAR(256) NOT NULL,
            email VARCHAR(256) NOT NULL,
            PRIMARY KEY (username)
        )
        """,
            """
        CREATE TABLE IF NOT EXISTS games (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(256),
            blackUsername VARCHAR(256),
            gameName VARCHAR(256) NOT NULL,
            game TEXT NOT NULL,
            PRIMARY KEY (gameID)
        )
        """,
            """
        CREATE TABLE IF NOT EXISTS auth (
            username VARCHAR(256) NOT NULL,
            authToken VARCHAR(256) NOT NULL,
            PRIMARY KEY (authToken)
        )
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            for (var table : new String[]{"auth", "games", "users"}) {
                try (var ps = conn.prepareStatement("TRUNCATE TABLE " + table)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Clear failed: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
            ps.setString(1, user.username());
            ps.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
            ps.setString(3, user.email());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("createUser failed: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("SELECT username, password, email FROM users WHERE username=?");
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("getUser failed: " + e.getMessage());
        }
        return null;
    }
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)");
            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("createAuth failed: " + e.getMessage());
        }
    }
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("SELECT username, authToken FROM auth WHERE authToken=?");
            ps.setString(1, authToken);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new AuthData(rs.getString("username"), rs.getString("authToken"));
            }
        } catch (
                SQLException e) {
            throw new DataAccessException("getAuth failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("DELETE FROM auth WHERE authToken=?");
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("deleteAuth failed: " + e.getMessage());
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(
                    "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, new Gson().toJson(game.game()));
            ps.executeUpdate();
            var keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            throw new DataAccessException("Creating game failed, no ID obtained");
        } catch (SQLException e) {
            throw new DataAccessException("createGame failed: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?");
            ps.setInt(1, gameID);
            var rs = ps.executeQuery();
            if (rs.next()) {
                var chessGame = new Gson().fromJson(rs.getString("game"), chess.ChessGame.class);
                return new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        chessGame);
            }
        } catch (SQLException e) {
            throw new DataAccessException("getGame failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games");
            var rs = ps.executeQuery();
            while (rs.next()) {
                var chessGame = new Gson().fromJson(rs.getString("game"), chess.ChessGame.class);
                result.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        chessGame));
            }
        } catch (SQLException e) {
            throw new DataAccessException("listGames failed: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(
                    "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?");
            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, new Gson().toJson(game.game()));
            ps.setInt(5, game.gameID());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("Game not found: " + game.gameID());
            }
        } catch (SQLException e) {
            throw new DataAccessException("updateGame failed: " + e.getMessage());
        }
    }
}