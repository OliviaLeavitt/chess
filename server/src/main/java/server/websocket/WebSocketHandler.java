package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import exception.ResponseException;
import model.Auth;
import model.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public WebSocketHandler(GameDAO gameDao, AuthDAO authDao) {
        this.gameDAO = gameDao;
        this.authDAO = authDao;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, InvalidMoveException, ResponseException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.commandType()) {
            case CONNECT -> connect(userGameCommand.authToken(), session, userGameCommand.gameID());
            case MAKE_MOVE -> makeMove(userGameCommand, userGameCommand.authToken());
            case LEAVE -> leave(userGameCommand.authToken());
            case RESIGN -> resign(userGameCommand.authToken());
        }
    }

    private void connect(String authToken, Session session, int gameId) throws IOException, ResponseException {
        Auth authData = authDAO.getAuth(authToken);
        if (authData == null) {
            var errorMessage = "Invalid authentication token.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            session.getRemote().sendString(new Gson().toJson(errorServerMessage));
            return;
        }
        String userName = authData.username();

        Game game = gameDAO.getGame(gameId);
        if (game == null) {
            var errorMessage = "Invalid gameId.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            session.getRemote().sendString(new Gson().toJson(errorServerMessage));
            return;
        }

        // Add the new player (root client) to the connections list
        connections.add(userName, session, gameId);

        // Send the game state to the newly joined (root) player
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);
        connections.sendOneMessage(userName, loadGameMessage);

        // Notify everyone else that the root client has joined, excluding the newly joined player
        String joinMessage = String.format("%s has joined the game.", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, joinMessage, null);
        connections.broadcast(userName, notification);

    }


    private void makeMove(UserGameCommand command, String authToken) throws IOException, InvalidMoveException, ResponseException {
        Auth authData = authDAO.getAuth(authToken);
        String userName = authData.username();
        int gameId = command.gameID();
        Game game = gameDAO.getGame(gameId);

        game.game().makeMove(command.move());

        // Send updated game state to the root client (the player who made the move)
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);
        connections.broadcast("", loadGameMessage);

        // Send a notification to all other clients in the game (excluding the root client)
        String moveMessage = String.format("Move made: %s", command.move());
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, moveMessage, null);
        connections.broadcast(userName, notification);
    }

    private void leave(String userName) throws IOException {
        connections.remove(userName);
        var message = String.format("%s has left the game.", userName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, null);
        connections.broadcast(userName, serverMessage);
    }



    private void resign(String userName) throws IOException {
        var message = String.format("%s has resigned from the game.", userName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, null);
        connections.broadcast(userName, serverMessage);
    }

}
