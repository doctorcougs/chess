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


}
