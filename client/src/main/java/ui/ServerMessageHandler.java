package ui;

import websocket.messages.*;

public interface ServerMessageHandler {
    void notifyLoadGame(LoadGameMessage message);
    void notifyNotification(NotificationMessage message);
    void notifyError(ErrorMessage message);
}
