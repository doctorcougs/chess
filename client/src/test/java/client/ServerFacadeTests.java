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
}