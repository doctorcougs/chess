package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private MemoryDataAccess dataAccess;
    private ClearService clearService;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        clearService = new ClearService(dataAccess);
    }

    @Test
    void clearRemovesAllData() throws DataAccessException {
        dataAccess.createUser(new UserData("coug", "pass", "email.com"));

        clearService.clear();

        assertNull(dataAccess.getUser("coug"));
    }

    @Test
    void clearOnEmptyDatabaseSucceeds() {
        // Should not throw even when nothing is stored
        assertDoesNotThrow(() -> clearService.clear());
    }
}