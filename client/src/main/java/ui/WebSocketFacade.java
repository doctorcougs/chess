package ui;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketFacade {
    private Session session;
    private final Gson gson = new Gson();
    private final ServerMessageHandler handler;

    public WebSocketFacade(String serverUrl, ServerMessageHandler handler) throws Exception {
        this.handler = handler;
        URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, uri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        ServerMessage base = gson.fromJson(message, ServerMessage.class);
        switch (base.getServerMessageType()) {
            case LOAD_GAME -> handler.onLoadGame(gson.fromJson(message, LoadGameMessage.class));
            case NOTIFICATION -> handler.onNotification(gson.fromJson(message, NotificationMessage.class));
            case ERROR -> handler.onError(gson.fromJson(message, ErrorMessage.class));
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        this.session = null;
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void close() throws Exception {
        if (session != null) {
            session.close();
        }
    }
}