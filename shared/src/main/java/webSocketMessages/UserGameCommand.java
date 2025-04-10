package webSocketMessages;

import chess.ChessMove;
import com.google.gson.Gson;

public record UserGameCommand(
        CommandType commandType, String authToken,
        Integer gameID, ChessMove move) {

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
