package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {
    private MemoryDataAccess dataAccess;
    private CreateGameService service;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        service = new CreateGameService(dataAccess);
    }

    @Test
    void createGameSuccessCreatesGame() throws DataAccessException {
        var auth = new AuthData("alice", "token-1");
        dataAccess.createAuth(auth);

        var result = service.createGame(auth.authToken(), new CreateGameService.CreateGameRequest("myGame"));

        assertTrue(result.gameID() > 0);
        assertNotNull(dataAccess.getGame(result.gameID()));
        assertEquals("myGame", dataAccess.getGame(result.gameID()).gameName());
    }

    @Test
    void createGameUnauthorizedThrows() {
        assertThrows(DataAccessException.class,
                () -> service.createGame("bad-token", new CreateGameService.CreateGameRequest("myGame")));
    }
}