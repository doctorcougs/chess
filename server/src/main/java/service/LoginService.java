package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class LoginService {
    private final DataAccess dataAccess;

    public LoginService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request == null) {
            throw new DataAccessException("Missing request body");
        }
        if (isBlank(request.username()) || isBlank(request.password())) {
            throw new DataAccessException("Missing required fields");
        }

        UserData user = dataAccess.getUser(request.username());
        if (user == null) {
            throw new DataAccessException("Unauthorized");
        }
        if (!request.password().equals(user.password())) {
            throw new DataAccessException("Unauthorized");
        }

        AuthData auth = new AuthData(user.username(), UUID.randomUUID().toString());
        dataAccess.createAuth(auth);

        return new LoginResult(auth.username(), auth.authToken());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}