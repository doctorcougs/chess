package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    private final Map<Integer, Set<WsContext>> gameSessions = new ConcurrentHashMap<>();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void onConnect(WsConnectContext ctx) {
        // required by javalin
    }

    public void onMessage(WsMessageContext ctx) {
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT  -> handleConnect(ctx, command);
            case MAKE_MOVE -> handleMakeMove(ctx, gson.fromJson(ctx.message(), MakeMoveCommand.class));
            case LEAVE    -> handleLeave(ctx, command);
            case RESIGN   -> handleResign(ctx, command);
        }
    }

    public void onClose(WsCloseContext ctx) {
        // Remove this session from all games when connection drops
        gameSessions.values().forEach(sessions -> sessions.remove(ctx));
    }

    // connecting to game
    private void handleConnect(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: unauthorized auth token");
                return;
            }
            GameData game = dataAccess.getGame(command.getGameID());
            if (game == null) {
                sendError(ctx, "Error: no game found");
                return;
            }

            gameSessions.computeIfAbsent(command.getGameID(), k -> ConcurrentHashMap.newKeySet()).add(ctx);

            sendMessage(ctx, gson.toJson(new LoadGameMessage(game.game())));

            String team;
            if (auth.username().equals(game.whiteUsername())) {
                team = auth.username() + " is WHITE";
            } else if (auth.username().equals(game.blackUsername())) {
                team = auth.username() + " is BLACK";
            } else {
                team = auth.username() + " is an observer";
            }
            broadcastToOthers(command.getGameID(), ctx, gson.toJson(new NotificationMessage(team)));

        } catch (DataAccessException e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    // Helper functions:

    private void sendMessage(WsContext ctx, String message) {
        ctx.send(message);
    }

    private void sendError(WsContext ctx, String errorMessage) {
        ctx.send(gson.toJson(new ErrorMessage(errorMessage)));
    }

    private void broadcastToAll(int gameID, String message) {
        Set<WsContext> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            sessions.forEach(s -> s.send(message));
        }
    }

    private void broadcastToOthers(int gameID, WsContext exclude, String message) {
        Set<WsContext> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            sessions.stream()
                    .filter(s -> !s.equals(exclude))
                    .forEach(s -> s.send(message));
        }
    }
}
