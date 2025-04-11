package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    private String key(String userName, int gameId) {
        return userName + ":" + gameId;
    }

    public void add(String userName, Session session, int gameId) {
        var connection = new Connection(userName, session, gameId);
        connections.put(key(userName, gameId), connection);
    }

    public void remove(String userName, int gameId) {
        connections.remove(key(userName, gameId));
    }

    public void broadcast(String excludeUserName, ServerMessage serverMessage, int gameId) throws IOException {
        var removeList = new ArrayList<String>();
        for (var entry : connections.entrySet()) {
            var conn = entry.getValue();
            if (conn.session.isOpen() && conn.gameId == gameId) {
                if (!conn.userName.equals(excludeUserName)) {
                    conn.send(new Gson().toJson(serverMessage));
                }
            } else if (!conn.session.isOpen()) {
                removeList.add(entry.getKey());
            }
        }

        for (var key : removeList) {
            connections.remove(key);
        }
    }

    public void sendOneMessage(String userName, int gameId, ServerMessage serverMessage) throws IOException {
        Connection connection = connections.get(key(userName, gameId));
        if (connection != null) {
            connection.send(new Gson().toJson(serverMessage));
        }
    }
}
