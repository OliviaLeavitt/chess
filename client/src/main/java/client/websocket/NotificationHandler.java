package client.websocket;

import webSocketMessages.ServerMessage;

public interface NotificationHandler {
    void handleServerMessage(ServerMessage message);
}
