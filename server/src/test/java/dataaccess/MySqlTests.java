package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlTests {

    private MySqlDataAccess dataAccess;

    @BeforeEach
    void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.clear();
    }

    @Test
    void clearSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("cougar", "cougar", "cougar@email.com"));
        dataAccess.createAuth(new AuthData("cougar", "cougarAuth"));
        dataAccess.createGame(new GameData(0, null, null, "cougarGame", new ChessGame()));

        dataAccess.clear();

        assertNull(dataAccess.getUser("cougar"));
        assertNull(dataAccess.getAuth("cougarAuth"));
        assertTrue(dataAccess.listGames().isEmpty());
    }

    @Test
    void createUserSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        UserData result = dataAccess.getUser("coug");
        assertNotNull(result);
        assertEquals("coug", result.username());
    }

    @Test
    void noDuplicateUsers() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        assertThrows(DataAccessException.class,
                () -> dataAccess.createUser(new UserData("coug", "coug2", "coug2@byu.edu")));
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        UserData result = dataAccess.getUser("coug");
        assertNotNull(result);
        assertEquals("coug@byu.edu", result.email());
        assertTrue(BCrypt.checkpw("coug", result.password()));
    }

    @Test
    void getUserNull() throws DataAccessException {
        UserData result = dataAccess.getUser("nonexistent");
        assertNull(result);
    }

    @Test
    void createAuthSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        dataAccess.createAuth(new AuthData("coug", "cougAuth"));
        AuthData result = dataAccess.getAuth("cougAuth");
        assertNotNull(result);
        assertEquals("coug", result.username());
    }

    @Test
    void createAuthDuplicate() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        dataAccess.createAuth(new AuthData("coug", "cougAuth"));
        assertThrows(DataAccessException.class,
                () -> dataAccess.createAuth(new AuthData("coug", "cougAuth")));
    }

    @Test
    void getAuthSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        dataAccess.createAuth(new AuthData("coug", "cougAuth"));
        AuthData result = dataAccess.getAuth("cougAuth");
        assertNotNull(result);
        assertEquals("cougAuth", result.authToken());
        assertEquals("coug", result.username());
    }

    @Test
    void getAuthNull() throws DataAccessException {
        AuthData result = dataAccess.getAuth("BADCOUG");
        assertNull(result);
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        dataAccess.createAuth(new AuthData("coug", "cougAuth"));
        dataAccess.deleteAuth("cougAuth");
        assertNull(dataAccess.getAuth("cougAuth"));
    }

    @Test
    void deleteAuthNull() {
        assertDoesNotThrow(() -> dataAccess.deleteAuth("BADCOUG"));
    }

    @Test
    void updateGameSuccess() throws DataAccessException {
        int id = dataAccess.createGame(new GameData(0, null, null, "cougGame", new ChessGame()));
        GameData updated = new GameData(id, "white", "black", "cougGame", new ChessGame());
        dataAccess.updateGame(updated);
        GameData result = dataAccess.getGame(id);
        assertEquals("white", result.whiteUsername());
        assertEquals("black", result.blackUsername());
    }

    @Test
    void updateGameNull() {
        GameData fake = new GameData(100000, "white", "black", "badCougGame", new ChessGame());
        assertThrows(DataAccessException.class, () -> dataAccess.updateGame(fake));
    }

    @Test
    void createHashedPass() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        UserData result = dataAccess.getUser("coug");
        assertNotNull(result);
        assertNotEquals("coug", result.password());
        assertTrue(BCrypt.checkpw("coug", result.password()));
    }

    @Test
    void createMultipleAuths() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "coug", "coug@byu.edu"));
        dataAccess.createAuth(new AuthData("coug", "cougAuth1"));
        dataAccess.createAuth(new AuthData("coug", "cougAuth2"));
        assertNotNull(dataAccess.getAuth("cougAuth1"));
        assertNotNull(dataAccess.getAuth("cougAuth2"));
    }

    @Test
    void updateGamePersistsPlayers() throws DataAccessException {
        int id = dataAccess.createGame(new GameData(0, null, null, "cougarGame", new ChessGame()));
        dataAccess.updateGame(new GameData(id, "white", "black", "cougarGame", new ChessGame()));
        GameData result = dataAccess.getGame(id);
        assertEquals("white", result.whiteUsername());
        assertEquals("black", result.blackUsername());
    }

    @Test
    void updateGameNotFoundThrows() {
        assertThrows(DataAccessException.class,
                () -> dataAccess.updateGame(new GameData(99999, "white", "black", "FakeGame", new ChessGame())));
    }

    @Test
    void updateGamePersists() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        int id = dataAccess.createGame(new GameData(0, null, null, "CougarGame", chessGame));
        ChessGame updatedGame = new ChessGame();
        updatedGame.setTeamTurn(ChessGame.TeamColor.BLACK);
        dataAccess.updateGame(new GameData(id, null, null, "CougarGame", updatedGame));
        GameData result = dataAccess.getGame(id);
        assertNotNull(result.game());
        assertEquals(ChessGame.TeamColor.BLACK, result.game().getTeamTurn());
    }

    @Test
    void listGamesContainsCorrectNames() throws DataAccessException {
        dataAccess.createGame(new GameData(0, null, null, "cougarA", new ChessGame()));
        dataAccess.createGame(new GameData(0, null, null, "cougarB", new ChessGame()));
        List<GameData> games = dataAccess.listGames();
        assertTrue(games.stream().anyMatch(g -> g.gameName().equals("cougarA")));
        assertTrue(games.stream().anyMatch(g -> g.gameName().equals("cougarB")));
    }

    @Test
    void getUserAfterClearReturnsNull() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "pass", "coug@byu.edu"));
        dataAccess.clear();
        assertNull(dataAccess.getUser("coug"));
    }
}