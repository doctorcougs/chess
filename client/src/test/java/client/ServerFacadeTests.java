package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static UserData validUser = new UserData("Cougar", "Cougar", "Cougar@Cougar");
    private static UserData invalidUser = new UserData("Cougar", null, "Cougar@Cougar");
    GameData validGame = new GameData(1,null, null, "CougarGame", null);
    GameData invalidGame = new GameData(-1,null, null, "CougarGame", null);


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        serverFacade.clear();
    }


    @Test
    public void testPositiveRegisterLogin() throws Exception{
            serverFacade.register(validUser);
            var authData = serverFacade.login(validUser);
            Assertions.assertNotNull(authData);
    }

    @Test void testNegativeRegister() throws Exception{
        Assertions.assertThrows(Exception.class, () -> serverFacade.register(invalidUser));
    }

    @Test void testNegativeLogin() throws Exception{
        Assertions.assertThrows(Exception.class, () -> serverFacade.login(invalidUser));
    }

    @Test void testDuplicateRegister() throws Exception {
        serverFacade.register(validUser);
        Assertions.assertThrows(Exception.class, () -> serverFacade.register(validUser));
    }

    @Test void testPositveCreateGame() throws Exception {
        AuthData authData = serverFacade.register(validUser);
        GameData gameData = serverFacade.createGame(authData.authToken(), validGame);
        Assertions.assertNotNull(gameData);
    }

    @Test void testNegativeCreateGame() throws Exception {
        AuthData authData = serverFacade.register(validUser);
        Assertions.assertThrows(Exception.class, () -> serverFacade.createGame("invalidToken", validGame));
    }

    @Test void testPositiveListGames() throws Exception {
        AuthData authData = serverFacade.register(validUser);
        serverFacade.createGame(authData.authToken(), validGame);
        var games = serverFacade.listGames(authData.authToken());
        Assertions.assertNotNull(games);
    }

    @Test void testNegativeListGames() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.listGames("invalidToken"));
    }

    @Test void testPositivePlayGame() throws Exception {
        AuthData authData = serverFacade.register(validUser);
        GameData gameData = serverFacade.createGame(authData.authToken(), validGame);
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(authData.authToken(), gameData.gameID(), "WHITE"));
    }

    @Test void testDuplicateTeamPlayGame() throws Exception {
        AuthData authData = serverFacade.register(validUser);
        GameData gameData = serverFacade.createGame(authData.authToken(), validGame);
        serverFacade.joinGame(authData.authToken(), gameData.gameID(), "WHITE");
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(authData.authToken(), gameData.gameID(), "WHITE"));
    }

    @Test void testWrongIDPlayGame() throws Exception{
        AuthData authData = serverFacade.register(validUser);
        GameData gameData = serverFacade.createGame(authData.authToken(), validGame);
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(authData.authToken(), gameData.gameID() + 1, "WHITE"));
    }
    @Test void testPositiveLogout() throws Exception {
        var authData = serverFacade.register(validUser);
        Assertions.assertDoesNotThrow(()-> serverFacade.logout(authData.authToken()));
    }

    @Test
    void negativeLogout() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(null));
    }
}

