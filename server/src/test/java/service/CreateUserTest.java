package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest {
    private MemoryDataAccess dataAccess;
    private CreateUser createUser;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        createUser = new CreateUser(dataAccess);
    }

    @Test
    void registerSuccessCreatesUserAndAuth() throws DataAccessException {
        var user = new UserData("coug", "pass", "email.com");

        AuthData auth = createUser.register(user);

        assertNotNull(auth);
        assertEquals("coug", auth.username());
        assertNotNull(auth.authToken());

        assertNotNull(dataAccess.getUser("coug"));
        assertNotNull(dataAccess.getAuth(auth.authToken()));
        assertEquals("coug", dataAccess.getAuth(auth.authToken()).username());
    }

    @Test
    void registerDuplicateUsernameThrows() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "pass", "email.com"));

        var ex = assertThrows(DataAccessException.class,
                () -> createUser.register(new UserData("coug", "newpass", "newemail.com")));

        assertTrue(ex.getMessage().toLowerCase().contains("taken"));
    }
}