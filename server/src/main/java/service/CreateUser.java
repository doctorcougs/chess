package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class CreateUser {
    private final DataAccess dataAccess;

    public CreateUser(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * Registers a new user and returns an auth token.
     */
    public AuthData register(UserData user) throws DataAccessException {
        if (user == null) {
            throw new DataAccessException("Missing request body");
        }
        if (isBlank(user.username()) || isBlank(user.password()) || isBlank(user.email())) {
            throw new DataAccessException("Missing required fields");
        }

        var existing = dataAccess.getUser(user.username());
        if (existing != null) {
            throw new DataAccessException("Username already taken");
        }

        dataAccess.createUser(user);

        var auth = new AuthData(user.username(), UUID.randomUUID().toString());
        dataAccess.createAuth(auth);

        return auth;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}