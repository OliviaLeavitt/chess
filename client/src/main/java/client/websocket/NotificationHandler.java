package client.websocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void handleServerMessage(ServerMessage message);
}
