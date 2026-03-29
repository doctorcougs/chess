package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            authToken VARCHAR(256) NOT NULL,
            username VARCHAR(256) NOT NULL,
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
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
    }
}