package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class LogoutService {
    private final DataAccess dataAccess;

    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Unauthorized");
        }
        if (dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }

        dataAccess.deleteAuth(authToken);
    }
}