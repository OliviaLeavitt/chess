package webSocketMessages;

import chess.ChessGame;

public class ServerMessage {
    private ChessGame game;

    public ServerMessage(ChessGame game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
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