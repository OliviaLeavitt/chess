package webSocketMessages;

import com.google.gson.Gson;

public record Action(Type commandType, String authToken, Integer gameID) {
    public enum Type {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
