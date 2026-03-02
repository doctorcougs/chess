package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;

public class CreateGameService {
    private final DataAccess dataAccess;

    public CreateGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public record CreateGameRequest(String gameName) {}
    public record CreateGameResult(int gameID) {}

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Unauthorized");
        }
        if (dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }
        if (request == null) {
            throw new DataAccessException("Missing request body");
        }
        if (request.gameName() == null || request.gameName().isBlank()) {
            throw new DataAccessException("Missing gameName");
        }

        var newGame = new GameData(
                0,
                null,
                null,
                request.gameName(),
                new ChessGame()
        );

        int id = dataAccess.createGame(newGame);
        return new CreateGameResult(id);
    }
}