package ui;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface ServerMessageHandler {
    void onLoadGame(LoadGameMessage message);
    void onNotification(NotificationMessage message);
    void onError(ErrorMessage message);
}