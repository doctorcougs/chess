package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {
    private MemoryDataAccess dataAccess;
    private CreateGameService createGameService;
    private JoinGameService joinGameService;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        createGameService = new CreateGameService(dataAccess);
        joinGameService = new JoinGameService(dataAccess);
    }

    @Test
    void joinGameSuccessClaimsWhite() throws DataAccessException {
        dataAccess.createAuth(new AuthData("coug", "token-a"));
        int gameID = createGameService.createGame("token-a", new CreateGameService.CreateGameRequest("g1")).gameID();

        joinGameService.joinGame("token-a", new JoinGameService.JoinGameRequest("WHITE", gameID));

        var updated = dataAccess.getGame(gameID);
        assertEquals("coug", updated.whiteUsername());
        assertNull(updated.blackUsername());
    }

    @Test
    void joinGameColorAlreadyTakenThrows() throws DataAccessException {
        dataAccess.createAuth(new AuthData("coug", "token-a"));
        dataAccess.createAuth(new AuthData("cougie", "token-b"));
        int gameID = createGameService.createGame("token-a", new CreateGameService.CreateGameRequest("g1")).gameID();

        joinGameService.joinGame("token-a", new JoinGameService.JoinGameRequest("WHITE", gameID));

        assertThrows(DataAccessException.class,
                () -> joinGameService.joinGame("token-b", new JoinGameService.JoinGameRequest("WHITE", gameID)));
    }
}