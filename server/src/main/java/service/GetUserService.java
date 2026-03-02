package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;

public class GetUserService {
    private final DataAccess dataAccess;

    public GetUserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData getUser(String username) throws DataAccessException {
        if (username == null || username.isBlank()) {
            throw new DataAccessException("Missing username");
        }

        UserData user = dataAccess.getUser(username);
        if (user == null) {
            throw new DataAccessException("User not found");
        }

        return user;
    }
}