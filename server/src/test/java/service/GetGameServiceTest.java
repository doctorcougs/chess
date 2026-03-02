package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetGameServiceTest {
    private MemoryDataAccess dataAccess;
    private CreateGameService createGameService;
    private GetGameService getGameService;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        createGameService = new CreateGameService(dataAccess);
        getGameService = new GetGameService(dataAccess);
    }

    @Test
    void getGameSuccessReturnsGame() throws DataAccessException {
        dataAccess.createAuth(new AuthData("coug", "token-1"));
        int gameID = createGameService
                .createGame("token-1", new CreateGameService.CreateGameRequest("myGame"))
                .gameID();

        var game = getGameService.getGame(gameID);

        assertNotNull(game);
        assertEquals(gameID, game.gameID());
        assertEquals("myGame", game.gameName());
    }

    @Test
    void getGameMissingThrows() {
        assertThrows(DataAccessException.class, () -> getGameService.getGame(999));
    }
}