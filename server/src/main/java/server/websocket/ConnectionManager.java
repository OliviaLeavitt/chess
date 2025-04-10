package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String userName, Session session, int gameId) {
        var connection = new Connection(userName, session, gameId);
        connections.put(userName, connection);
    }

    public void remove(String userName) {
        connections.remove(userName);
    }
    public void broadcast(String excludeUserName, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.userName.equals(excludeUserName)) {
                    c.send(serverMessage.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.userName);
        }
    }
}
