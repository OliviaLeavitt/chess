package client.websocket;

import webSocketMessages.Notification;
import webSocketMessages.ServerMessage;

public interface NotificationHandler {
    void handleServerMessage(ServerMessage message);
}
