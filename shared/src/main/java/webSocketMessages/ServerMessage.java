package webSocketMessages;

import chess.ChessGame;

public class ServerMessage {
    private ChessGame game;
    private String errorMessage;
    private String notificationMessage;

    public ServerMessage(ServerMessageType serverMessageType) {
        this.serverMessageType = serverMessageType;

    }

    public ServerMessage(ServerMessageType serverMessageType, String message) {
        this.serverMessageType = serverMessageType;
        if (serverMessageType == ServerMessageType.ERROR) {
            this.errorMessage = message;
        } else if (serverMessageType == ServerMessageType.NOTIFICATION) {
            this.notificationMessage = message;
        }
    }

    public ChessGame getGame() {
        return game;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessageType serverMessageType;
}