package client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import exception.ResponseException;
import model.Game;
import model.User;
import results.CreateResult;
import results.LoginResult;
import server.ServerFacade;
import ui.DrawChessBoard;
import websocket.messages.ServerMessage;


public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private String authToken = null;
    private WebSocketFacade webSocketFacade;
    private final String serverUrl;
    private Game currentGame;
    private int currentgameId = 0;
    private State state = State.PRELOGIN;
    private final Gson gson = new Gson();
    private NotificationHandler notificationHandler;
    private String userName;
    private Game[] games;

    public ChessClient(String serverUrl) throws ResponseException {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        this.webSocketFacade = new WebSocketFacade(serverUrl, this);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.PRELOGIN) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            } else if (state == State.POSTLOGIN) {
                return switch (cmd) {
                    case "logout" -> logout();
                    case "creategame" -> createGame(params);
                    case "listgames" -> listGames();
                    case "playgame" -> playGame(params);
                    case "observegame" -> observeGame(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            } else if (state == State.INGAME) {
                return switch (cmd) {
                    case "makemove" -> makeMove();
                    case "redrawboard" -> redrawBoard();
                    case "leave" -> leaveGame();
                    case "resign" -> resign();
                    case "highlightmoves" -> highlightLegalMoves();
                    default -> help();
                };

            }
            else {
                throw new IllegalStateException("Invalid client state: " + state);
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String highlightLegalMoves() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the position of the piece (ex: e1): ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.length() != 2) {
            return "Invalid number of arguments. Please use the format like e2.";
        }

        char colChar = input.charAt(0);
        char rowChar = input.charAt(1);

        if (colChar < 'a' || colChar > 'h' || rowChar < '1' || rowChar > '8') {
            return "Invalid position. Columns must be a-h and rows must be 1-8.";
        }

        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);

        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = currentGame.game().getBoard().getPiece(position);

        if (piece == null) {
            return "There is no piece at that position.";
        }

        Collection<ChessMove> validMoves = currentGame.game().validMoves(position);

        if (validMoves.isEmpty()) {
            return "No legal moves available for this piece.";
        }

        String validMovesString = "Here are your valid move options:\n";
        for (ChessMove move : validMoves) {
            validMovesString += move.toString() + "\n";
        }

        DrawChessBoard.drawChessboard(currentGame, currentGame.game().getTeamTurn().toString(), validMoves);

        return validMovesString;
    }

    private String leaveGame() throws ResponseException {
        this.webSocketFacade.leave(authToken, currentgameId, userName);
        state = State.POSTLOGIN;
        return "You have left the game.";
    }

    private String resign() throws ResponseException {
        this.webSocketFacade.resign(authToken, currentgameId, userName);
        state = State.POSTLOGIN;
        return "You have resigned from the game.";
    }


    private String makeMove() {
        Scanner makeMoveScanner = new Scanner(System.in);
        System.out.print("Enter your move (e.g., pawn a2a3 or a7a8 queen): ");
        String userMoveInput = makeMoveScanner.nextLine();

        String[] userInputArray = userMoveInput.split(" ");

        if (userInputArray.length < 2 || userInputArray[1].length() != 4) {
            return "Invalid format: Please enter the start and end squares (e.g., a2a3). If promoting, add the new piece (e.g., a7a8 queen).";
        }

        char startColChar = userInputArray[1].charAt(0);
        char endColChar = userInputArray[1].charAt(2);
        char startRowChar = userInputArray[1].charAt(1);
        char endRowChar = userInputArray[1].charAt(3);

        if (startColChar < 'a' || startColChar > 'h' || endColChar < 'a' || endColChar > 'h' ||
                startRowChar < '1' || startRowChar > '8' || endRowChar < '1' || endRowChar > '8') {
            return "Invalid move. Columns must be a-h, and rows must be 1-8.";
        }

        try {
            int startCol = (startColChar - 'a') + 1;
            int startRow = (Character.getNumericValue(startRowChar) - 1) + 1;
            int endCol = (endColChar - 'a') + 1;
            int endRow = (Character.getNumericValue(endRowChar) - 1) + 1;

            ChessPosition startPosition = new ChessPosition(startRow, startCol);
            ChessPosition endPosition = new ChessPosition(endRow, endCol);

            ChessPiece.PieceType pieceType = ChessPiece.PieceType.valueOf(userInputArray[0].toUpperCase());

            ChessPiece.PieceType promotionPiece = null;
            if (userInputArray.length == 3) {
                String promotionInput = userInputArray[2].toLowerCase();
                if (promotionInput.equals("queen") || promotionInput.equals("rook") ||
                        promotionInput.equals("bishop") || promotionInput.equals("knight")) {
                    promotionPiece = ChessPiece.PieceType.valueOf(promotionInput.toUpperCase());
                } else {
                    return "Invalid promotion piece! Choose from: QUEEN, ROOK, BISHOP, KNIGHT.";
                }
            }

            if (promotionPiece != null) {
                if ((startRow == 1 && endRow == 0) || (startRow == 6 && endRow == 7)) {
                    ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
                    this.webSocketFacade.makeMove(move, authToken, currentgameId, userName);
                    return "Pawn promoted to " + promotionPiece.toString().toLowerCase() + " and move executed successfully.";
                } else {
                    return "Promotion is only allowed when a pawn reaches the 8th row (for white) or the 1st row (for black).";
                }
            }

            ChessMove move = new ChessMove(startPosition, endPosition, null);


//            ChessPiece movingPiece = currentGame.getBoard().getPiece(startPosition);
//            Collection<ChessMove> validMoves = movingPiece.pieceMoves(currentGame.getBoard(), startPosition);
//            if (!validMoves.contains(move)) {
//                return "That move is invalid.";
//            }

            this.webSocketFacade.makeMove(move, authToken, currentGame.gameID(), userName);
            currentGame = server.getGame(currentGame.gameID());
            String currentTurn = currentGame.game().getTeamTurn().toString();

            DrawChessBoard.drawChessboard(currentGame, currentTurn, null);
            return "Move executed successfully.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    //get input to move piece to
    //create the move from input
    // send that move back to serverfacade
    // chessclient (creates move) -> serverfacade -> server -> service -> daos -> database



    private String redrawBoard() {
        String currentTurn = currentGame.game().getTeamTurn().toString();
        DrawChessBoard.drawChessboard(currentGame, currentTurn, null);
        return "Here is your board";

    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            User user = new User(username, password, email);
            var auth = server.register(user);
            authToken = auth.authToken();
            server.setAuthToken(authToken);
            state = State.POSTLOGIN;
            return String.format("Registered and logged in as %s.", user.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            var username = params[0];
            var password = params[1];
            LoginResult result = server.login(username, password);
            authToken = result.authToken();
            server.setAuthToken(authToken);
            state = State.POSTLOGIN;
            this.userName = username;
            return String.format("You logged in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String logout() throws ResponseException {
        server.logout(authToken);
        state = State.PRELOGIN;
        authToken = null;
        server.setAuthToken(null);
        return "You logged out.";
    }

    public String createGame(String... params) throws ResponseException, IOException, URISyntaxException {
        assertSignedIn();
        if (params.length == 1) {
            try {
                var gameName = params[0];
                CreateResult result = server.createGame(gameName);

                this.currentgameId = result.gameID();
                this.currentGame = result.game();
                return String.format("Created game: %s (with game id: %d)", gameName, currentgameId);
            } catch (ResponseException e) {
                System.out.println("failed in createGame in Client: " + e.getMessage());
            }


        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        this.games = server.listGames(authToken);
        var result = new StringBuilder();
        for (var game : games) {
            result.append("Game Name: ").append(game.gameName()).append(", Game ID: ").append(game.gameID()).append('\n');
        }
        return result.toString();
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            var gameId = Integer.parseInt(params[0]);
            DrawChessBoard.drawChessboard(currentGame, "WHITE", null);
            return String.format("You are now observing game %d.", gameId);
        }
        throw new ResponseException(400, "Expected: <gameID>");
    }

    public String playGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            var playerColor = params[0].toUpperCase();
            var gameId = Integer.parseInt(params[1]);

            if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                throw new ResponseException(400, "Error: Must be white or black");
            }

            server.joinGame(playerColor, gameId);

            state = State.INGAME;
            DrawChessBoard.drawChessboard(null, playerColor, null);
            return String.format("Joined game %d as player %s.", gameId, playerColor);
        }
        throw new ResponseException(400, "Error: Expected: playgame <WHITE or BLACK> <game number>");
    }

    public String help() {
        if (state == State.PRELOGIN) {
            return """
                    Available commands:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - to quit playing chess
                    help - to help with possible commands
                    """;
        } else if (state == State.INGAME) {
            return """
                    Available commands:
                    redrawboard - to redraw the chess board
                    leave - to leave the game
                    makemove - to make move in chess
                    resign -  to forfeit the game
                    highlightmoves - to highlight all legal moves
                    help - to help with possible commands
                    quit - to quit playing chess
                    """;
        }
        return """
                Available commands:
                logout - to log out of your account
                creategame <gameName> - to create a new game
                listgames - to list available games
                playgame <WHITE or BLACK> <game number> - to join a game as a player
                observegame <game number> - to observe a game
                help - to display available commands
                quit - to exit the program
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.PRELOGIN) {
            throw new ResponseException(400, "You must log in");
        }
    }


    public void handleServerMessage(ServerMessage message) {
        switch (message.serverMessageType) {
            case LOAD_GAME:
                this.currentGame = message.getGame();
                this.redrawBoard();
                break;
            case ERROR:
                System.out.println("Error: " + message.getErrorMessage());
                break;
            case NOTIFICATION:
                System.out.println("Notification: " + message.getNotificationMessage());
                break;
            default:
                System.out.println("Unknown message received in handleServerMessage in ChessClient.");
        }
    }
}