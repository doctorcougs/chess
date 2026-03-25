package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import java.util.List;

public class ListGamesService {
    private final DataAccess dataAccess;

    public ListGamesService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public record GameSummary(int gameID, String whiteUsername, String blackUsername, String gameName) {}
    public record ListGamesResult(List<GameSummary> games) {}

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Unauthorized");
        }
        if (dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }

        List<GameSummary> summaries = dataAccess.listGames().stream()
                .map(g -> new GameSummary(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName()))
                .toList();

        return new ListGamesResult(summaries);
    }
}