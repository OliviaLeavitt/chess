package webSocketMessages;

import chess.ChessGame;

public class ServerMessage {
    private ChessGame game;

    public ServerMessage(ServerMessageType serverMessageType) {
        this.serverMessageType = serverMessageType;
    }

    public ChessGame getGame() {
        return game;
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessageType serverMessageType;
}