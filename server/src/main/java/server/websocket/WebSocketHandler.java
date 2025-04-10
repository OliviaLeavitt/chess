package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.ServerMessage;
import webSocketMessages.UserGameCommand;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, InvalidMoveException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.commandType()) {
            case CONNECT -> enter(userGameCommand.userName(), session, userGameCommand.gameID());
            case MAKE_MOVE -> makeMove(userGameCommand);
            case LEAVE -> leave(userGameCommand.userName());
            case RESIGN -> resign(userGameCommand.userName());
        }
    }

    private void enter(String userName, Session session, int gameId) throws IOException {
        connections.add(userName, session, gameId);

        var message = String.format("%s has joined the game.", userName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(userName, serverMessage);
    }

    private void makeMove(UserGameCommand command) throws IOException, InvalidMoveException {

        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        ChessGame game = serverMessage.getGame();

        game.makeMove(command.move());

        var moveMessage = String.format("Move made: %s", command.move());

        connections.broadcast("", serverMessage);
    }

    private void leave(String userName) throws IOException {
        connections.remove(userName);
        var message = String.format("%s has left the game.", userName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(userName, serverMessage);
    }



    private void resign(String userName) throws IOException {
        var message = String.format("%s has resigned from the game.", userName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(userName, serverMessage);
    }

}
