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
import websocket.commands.MakeMoveCommand;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


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
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(makeMoveCommand, session, makeMoveCommand.authToken());
            }
            case LEAVE -> leave(userGameCommand);
            case RESIGN -> resign(userGameCommand.authToken(), session, userGameCommand.gameID());
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
        connections.add(userName, session, gameId);

        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);
        connections.sendOneMessage(userName, game.gameID(),loadGameMessage);

        String joinMessage = String.format("%s has joined the game.", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, joinMessage, null);
        connections.broadcast(userName, notification, gameId);

    }

    private void makeMove(MakeMoveCommand command, Session session, String authToken) throws IOException, ResponseException {
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
        ChessPosition start = command.getMove().getStartPosition();
        ChessPiece piece = game.game().getBoard().getPiece(start);
        ChessGame.TeamColor currentTurn = game.game().getTeamTurn();
        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();

        if (game.gameOver()) {
            var errorMessage = "can't move if game is over";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            session.getRemote().sendString(new Gson().toJson(errorServerMessage));
            return;
        }


        if ((currentTurn == ChessGame.TeamColor.WHITE && !userName.equals(whiteUser)) ||
                (currentTurn == ChessGame.TeamColor.BLACK && !userName.equals(blackUser))) {
            String errorMessage = "Invalid move: You can't move your opponent's piece. It's not your turn.";
            ServerMessage errorResponse = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.sendOneMessage(userName, game.gameID(), errorResponse);
            return;
        }

        try {
            game.game().makeMove(command.getMove());
            Game newGame = new Game(command.gameID(), whiteUser, blackUser, game.gameName(), game.game(), false);
            gameDAO.updateGame(newGame);
        } catch (InvalidMoveException e) {
            String errorMessage = "Invalid move: " + e.getMessage();
            ServerMessage invalidMoveMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.sendOneMessage(userName, game.gameID(), invalidMoveMessage);
            return;
        }

        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);
        connections.broadcast("", loadGameMessage, gameId);

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
            connections.broadcast("", notification, gameId);
        }

        String moveMessage = String.format("Move made: %s", command.getMove());
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, moveMessage, null);
        connections.broadcast(userName, notification, gameId);

    }


    private void leave(UserGameCommand command) throws IOException, ResponseException {
        Auth authData = authDAO.getAuth(command.authToken());
        if (authData == null) {
            var errorMessage = "Invalid authentication token.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.sendOneMessage(authData.username(), command.gameID(), errorServerMessage);
            return;
        }

        String userName = authData.username();

        Game game = gameDAO.getGame(command.gameID());
        if (game == null) {
            var errorMessage = "You are not in a game.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.sendOneMessage(userName, game.gameID(), errorServerMessage);
            return;
        }

        connections.remove(userName, game.gameID());

        String leaveMessage = String.format("%s has left the game.", userName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, leaveMessage, null);
        connections.broadcast(userName, serverMessage, game.gameID());

        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();
        if (userName.equals(game.blackUsername())) {
            Game newGame = new Game(game.gameID(), whiteUser, null, game.gameName(), game.game(), false);
            gameDAO.updateGame(newGame);
        } else if (userName.equals(game.whiteUsername())) {
            Game newGame = new Game(game.gameID(), null, blackUser, game.gameName(), game.game(), false);
            gameDAO.updateGame(newGame);
        }

    }



    private void resign(String authToken, Session session, int gameId) throws IOException, ResponseException {

        Auth authData = authDAO.getAuth(authToken);
        if (authData == null) {
            var errorMessage = "Invalid authentication token.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            session.getRemote().sendString(new Gson().toJson(errorServerMessage));
            return;
        }

        Game game = gameDAO.getGame(gameId);
        if (game == null) {
            var errorMessage = "There is no game to resign from.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            session.getRemote().sendString(new Gson().toJson(errorServerMessage));
            return;
        }

        if (game.gameOver()) {
            var errorMessage = "You can't double resign.";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            session.getRemote().sendString(new Gson().toJson(errorServerMessage));
            return;
        }

        String userName = authData.username();

        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();

        if (!userName.equals(whiteUser) && !userName.equals(blackUser)) {
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    "Only a player in the game can resign.", null, null);
            connections.sendOneMessage(userName, gameId, errorMessage);
            return;
        }

        if (!game.gameOver()) {

            if (userName.equals(game.blackUsername())) {
                Game newGame = new Game(gameId, whiteUser, null, game.gameName(), game.game(), true);
                gameDAO.updateGame(newGame);
            } else if (userName.equals(game.whiteUsername())) {
                Game newGame = new Game(gameId, null, blackUser, game.gameName(), game.game(), true);
                gameDAO.updateGame(newGame);
            }
        }

        String message = String.format("%s has resigned. Game over.", userName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, message, null);
        connections.broadcast("", serverMessage, gameId);
    }


}