package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private MemoryDataAccess dataAccess;
    private LoginService loginService;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        loginService = new LoginService(dataAccess);
    }

    @Test
    void loginSuccessReturnsAuthToken() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "pass", "email.com"));

        var result = loginService.login(new LoginService.LoginRequest("coug", "pass"));

        assertNotNull(result);
        assertEquals("coug", result.username());
        assertNotNull(result.authToken());
        assertNotNull(dataAccess.getAuth(result.authToken()));
    }

    @Test
    void loginWrongPasswordThrowsUnauthorized() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "pass", "email.com"));

        assertThrows(DataAccessException.class,
                () -> loginService.login(new LoginService.LoginRequest("coug", "wrong")));
    }
}