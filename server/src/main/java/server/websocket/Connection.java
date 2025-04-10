package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String userName;
    public Session session;
    public int gameId;

    public Connection(String userName, Session session, int gameId) {
        this.userName = userName;
        this.session = session;
        this.gameId = gameId;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}