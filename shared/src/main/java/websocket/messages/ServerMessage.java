package websocket.messages;

import model.Game;

public class ServerMessage {
    private Game game;
    private String errorMessage;
    private String message;

    public ServerMessage(ServerMessageType serverMessageType, String errorMessage, String notificationMessage, Game game) {
        this.serverMessageType = serverMessageType;
        this.errorMessage = errorMessage;
        this.message = notificationMessage;
        this.game = game;

    }

    public ServerMessage(ServerMessageType serverMessageType, String message) {
        this.serverMessageType = serverMessageType;
        if (serverMessageType == ServerMessageType.ERROR) {
            this.errorMessage = message;
        } else if (serverMessageType == ServerMessageType.NOTIFICATION) {
            this.message = message;
        }
    }

    public Game getGame() {
        return game;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getNotificationMessage() {
        return message;
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessageType serverMessageType;
}