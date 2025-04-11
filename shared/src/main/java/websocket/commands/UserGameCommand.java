package websocket.commands;

import chess.ChessMove;
import com.google.gson.Gson;

public class UserGameCommand {
    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    private CommandType commandType;
    private String authToken;
    private Integer gameID;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public CommandType commandType() {
        return commandType;
    }

    public String authToken() {
        return authToken;
    }

    public Integer gameID() {
        return gameID;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}