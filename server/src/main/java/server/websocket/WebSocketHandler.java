package server.websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
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
            case MAKE_MOVE -> makeMove(userGameCommand, session, userGameCommand.authToken());
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

    private void makeMove(UserGameCommand command, Session session, String authToken) throws IOException, ResponseException {
        Auth authData = authDAO.getAuth(authToken);

        if (authData == null) {
            var errorMessage = "Invalid authentication token.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            session.getRemote().sendString(new Gson().toJson(errorServerMessage));
            return;
        }

        String userName = authData.username();
        int gameId = command.gameID();
        Game game = gameDAO.getGame(gameId);
        ChessPosition start = command.move().getStartPosition();
        ChessPiece piece = game.game().getBoard().getPiece(start);
        ChessGame.TeamColor currentTurn = game.game().getTeamTurn();
        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();


        if ((currentTurn == ChessGame.TeamColor.WHITE && !userName.equals(whiteUser)) ||
                (currentTurn == ChessGame.TeamColor.BLACK && !userName.equals(blackUser))) {
            String errorMessage = "Invalid move: You can't move your opponent's piece. It's not your turn.";
            ServerMessage errorResponse = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.sendOneMessage(userName, errorResponse);
            return;
        }

        try {
            game.game().makeMove(command.move());
        } catch (InvalidMoveException e) {
            String errorMessage = "Invalid move: " + e.getMessage();
            ServerMessage invalidMoveMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.sendOneMessage(userName, invalidMoveMessage);
            return;
        }

        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);
        connections.broadcast("", loadGameMessage);

        ChessGame.TeamColor currentPlayerColor = game.game().getTeamTurn();
        boolean isCheck = game.game().isInCheck(currentPlayerColor);
        boolean isCheckmate = game.game().isInCheckmate(currentPlayerColor);
        boolean isStalemate = game.game().isInStalemate(currentPlayerColor);

        String gameStatusMessage = "";
        if (isCheckmate) {
            gameStatusMessage = "Checkmate! Game over.";
        } else if (isStalemate) {
            gameStatusMessage = "Stalemate! Game over.";
        } else if (isCheck) {
            gameStatusMessage = "Check! The king is in danger.";
        }

        if (!gameStatusMessage.isEmpty()) {
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, gameStatusMessage, null);
            connections.broadcast("", notification);
        } else {
            String moveMessage = String.format("Move made: %s", command.move());
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, moveMessage, null);
            connections.broadcast(userName, notification);
        }
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
