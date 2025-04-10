package webSocketMessages;

import chess.ChessGame;
import model.Game;

public class ServerMessage {
    private Game game;
    private String errorMessage;
    private String notificationMessage;

    public ServerMessage(ServerMessageType serverMessageType, String errorMessage, String notificationMessage, Game game) {
        this.serverMessageType = serverMessageType;
        this.errorMessage = errorMessage;
        this.notificationMessage = notificationMessage;
        this.game = game;

    }

    public ServerMessage(ServerMessageType serverMessageType, String message) {
        this.serverMessageType = serverMessageType;
        if (serverMessageType == ServerMessageType.ERROR) {
            this.errorMessage = message;
        } else if (serverMessageType == ServerMessageType.NOTIFICATION) {
            this.notificationMessage = message;
        }
    }

    public Game getGame() {
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