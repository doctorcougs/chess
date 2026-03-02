package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {
    private MemoryDataAccess dataAccess;
    private CreateGameService createGameService;
    private ListGamesService listGamesService;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        createGameService = new CreateGameService(dataAccess);
        listGamesService = new ListGamesService(dataAccess);
    }

    @Test
    void listGamesSuccessReturnsGames() throws DataAccessException {
        dataAccess.createAuth(new AuthData("coug", "token-1"));

        createGameService.createGame("token-1", new CreateGameService.CreateGameRequest("g1"));
        createGameService.createGame("token-1", new CreateGameService.CreateGameRequest("g2"));

        var result = listGamesService.listGames("token-1");

        assertNotNull(result);
        assertNotNull(result.games());
        assertEquals(2, result.games().size());
    }

    @Test
    void listGamesUnauthorizedThrows() {
        assertThrows(DataAccessException.class, () -> listGamesService.listGames("bad-token"));
    }
}