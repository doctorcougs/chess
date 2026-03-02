package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetUserServiceTest {
    private MemoryDataAccess dataAccess;
    private GetUserService service;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        service = new GetUserService(dataAccess);
    }

    @Test
    void getUserSuccessReturnsUser() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "pass", "email.com"));

        UserData user = service.getUser("coug");

        assertNotNull(user);
        assertEquals("coug", user.username());
        assertEquals("email.com", user.email());
    }

    @Test
    void getUserUserNotFoundThrows() {
        assertThrows(DataAccessException.class, () -> service.getUser("missingUser"));
    }
}