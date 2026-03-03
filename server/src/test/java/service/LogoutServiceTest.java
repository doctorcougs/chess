package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private MemoryDataAccess dataAccess;
    private LogoutService logoutService;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        logoutService = new LogoutService(dataAccess);
    }

    @Test
    void logoutSuccessDeletesAuth() throws DataAccessException {
        dataAccess.createAuth(new AuthData("coug", "token-1"));

        logoutService.logout("token-1");

        assertNull(dataAccess.getAuth("token-1"));
    }

    @Test
    void logoutInvalidTokenThrowsUnauthorized() {
        assertThrows(DataAccessException.class, () -> logoutService.logout("not-a-token"));
    }
}