// combines getGame and updateGame
package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class JoinGameService {
    private final DataAccess dataAccess;

    public JoinGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public record JoinGameRequest(String playerColor, int gameID) {}

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Unauthorized");
        }
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }
        if (request == null) {
            throw new DataAccessException("Missing request body");
        }
        if (request.gameID() <= 0) {
            throw new DataAccessException("Invalid gameID");
        }
        if (request.playerColor() == null || request.playerColor().isBlank()) {
            throw new DataAccessException("Missing playerColor");
        }

        GameData game = dataAccess.getGame(request.gameID());
        if (game == null) {
            throw new DataAccessException("Game not found");
        }

        String color = request.playerColor().trim().toUpperCase();
        String username = auth.username();

        GameData updated;
        switch (color) {
            case "WHITE" -> {
                if (game.whiteUsername() != null) {
                    throw new DataAccessException("Already taken");
                }
                updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            }
            case "BLACK" -> {
                if (game.blackUsername() != null) {
                    throw new DataAccessException("Already taken");
                }
                updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            }
            default -> throw new DataAccessException("Invalid playerColor");
        }

        dataAccess.updateGame(updated);
    }
}