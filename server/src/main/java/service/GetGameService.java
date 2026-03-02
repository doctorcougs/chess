package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;

public class GetGameService {
    private final DataAccess dataAccess;

    public GetGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (gameID <= 0) {
            throw new DataAccessException("Invalid gameID");
        }

        GameData game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }

        return game;
    }
}