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
            case CONNECT -> enter(userGameCommand.authToken(), session, userGameCommand.gameID());
            case MAKE_MOVE -> makeMove(userGameCommand);
            case LEAVE -> leave(userGameCommand.authToken());
            case RESIGN -> resign(userGameCommand.authToken());
        }
    }

    private void enter(String authToken, Session session, int gameId) throws IOException, ResponseException {
        Auth authData = authDAO.getAuth(authToken);
        if (authData == null) {
            var errorMessage = "Invalid authentication token.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.sendOneMessage(authToken, errorServerMessage);
            return;
        }
        String userName = authData.username();
        connections.add(userName, session, gameId);

        var message = String.format("%s has joined the game.", userName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, message, null);
        Game game = gameDAO.getGame(gameId);
        if (game == null) {
            var errorMessage = "Invalid gameId.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null); // make actual errormessage
            session.getRemote().sendString(new Gson().toJson(errorServerMessage));
            return;
        }
        ServerMessage clientMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);

        connections.sendOneMessage(userName, clientMessage);
        connections.broadcast(userName, serverMessage);
    }

    private void makeMove(UserGameCommand command) throws IOException, InvalidMoveException, ResponseException {

        int gameId = command.gameID();
        Game game = gameDAO.getGame(gameId);

        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);

        game.game().makeMove(command.move());

        var moveMessage = String.format("Move made: %s", command.move());

        connections.broadcast("", serverMessage);
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
