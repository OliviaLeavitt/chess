package client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

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
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static client.Repl.printPrompt;


public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private String authToken = null;
    private WebSocketFacade webSocketFacade;
    private final String serverUrl;
    private Game currentGame;
    private int currentgameId = 0;
    private State state = State.PRELOGIN;
    private String userName;
    private Game[] games;
    private boolean resigned = false;
    private String playerColor = null;
    private Map<Integer, Integer> gameIndexMap = new HashMap<>();

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
            } else if (state == State.OBSERVING) {
                    return switch (cmd) {
                    case "leave" -> leaveGame();
                    case "highlightmoves" -> highlightLegalMoves();
                    case "redrawboard" -> redrawBoard();
                    default -> help();
                };
            } else {
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

        StringBuilder validMovesString = new StringBuilder("Here are your valid move options:\n");
        for (ChessMove move : validMoves) {
            validMovesString.append(formatMove(move)).append("\n");
        }

        DrawChessBoard.drawChessboard(currentGame, playerColor, validMoves);

        return validMovesString.toString();
    }

    private char[] formatMove(ChessMove move) {
        String moveString = positionToString(move.getStartPosition()) + " to " + positionToString(move.getEndPosition());
        return moveString.toCharArray();
    }

    private String positionToString(ChessPosition startPosition) {
        char col = (char) ('a' + startPosition.getColumn() - 1);
        int row = startPosition.getRow();
        String string = "" + col + row;
        return string;
    }

    private String leaveGame() throws ResponseException {
        this.webSocketFacade.leave(authToken, currentgameId, userName);
        state = State.POSTLOGIN;
        return "You have left the game.";
    }

    private String resign() throws ResponseException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Are you sure you want to resign? (yes or no): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        switch (choice) {
            case "yes":
                this.webSocketFacade.resign(authToken, currentgameId, userName);
                this.resigned = true;
                return "You have successfully resigned.";

            case "no":
                return "Resignation has been canceled.";

            default:
                return "Invalid input. Please answer with 'yes' or 'no'.";
        }

    }


    private String makeMove() {

        if (resigned) {
            return "You can't make a move because this game has been resigned from";
        }

        String currentTurn = currentGame.game().getTeamTurn().toString();
        if (currentTurn.equals("WHITE") && !userName.equals(currentGame.whiteUsername())) {
            return "It's not your turn.";
        } else if (currentTurn.equals("BLACK") && !userName.equals(currentGame.blackUsername())) {
            return "It's not your turn.";
        }

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


            ChessPiece movingPiece = currentGame.game().getBoard().getPiece(startPosition);
            if (movingPiece == null) {
                return "Error: No piece selected to move!";
            }
            Collection<ChessMove> validMoves = currentGame.game().validMoves(startPosition);
            if (!validMoves.contains(move)) {
                return "That move is invalid.";
            }


            this.webSocketFacade.makeMove(move, authToken, currentgameId, userName);

            return "Move executed successfully.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


    private String redrawBoard() {
        String currentTurn = currentGame.game().getTeamTurn().toString();
        DrawChessBoard.drawChessboard(currentGame, playerColor, null);
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
            this.userName = username;
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
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: creategame <gamename>");
        }

        String gameName = params[0];
        CreateResult result = server.createGame(gameName);

        if (result == null || result.gameID() == 0) {
            return "Failed to create game.";
        }

        listGames();

        gameIndexMap.clear();
        for (int i = 0; i < games.length; i++) {
            gameIndexMap.put(i + 1, games[i].gameID());
        }

        return String.format("Game '%s' created with ID %d.", gameName, result.gameID());
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        this.games = server.listGames(authToken);
        gameIndexMap.clear();

        if (games.length == 0) {
            return "No games available.";
        }

        StringBuilder result = new StringBuilder("Here are the Games:\n");
        for (int i = 0; i < games.length; i++) {
            Game game = games[i];
            int displayNumber = i + 1;
            gameIndexMap.put(displayNumber, game.gameID());

            result.append(displayNumber);
            result.append(". Game Name: ").append(game.gameName());
            result.append(" - White: ").append(game.whiteUsername() != null ? game.whiteUsername() : "Open");
            result.append(" - Black: ").append(game.blackUsername() != null ? game.blackUsername() : "Open");
            result.append("\n");


        }
        return result.toString();
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (gameIndexMap.isEmpty()) {
            return "Please use the 'listgames' command before selecting a game to observe.";
        }
        if (params.length != 1) {
            return "observegame <game number>";
        }
        int index = Integer.parseInt(params[0]);

        if (!gameIndexMap.containsKey(index)) {
            return "Invalid game number.";
        }
        int gameId = gameIndexMap.get(index);

        this.playerColor = "WHITE";
        this.currentgameId = gameId;
        this.webSocketFacade.connect(authToken, currentgameId, userName);
        state = State.OBSERVING;
        return String.format("You are now observing game %d.", gameId);
    }
    public String playGame(String... params) throws ResponseException {
        assertSignedIn();
        if (gameIndexMap.isEmpty()) {
            return "Please use the 'listgames' command before selecting a game to play.";
        }
        if (params.length != 2) {
            return "playgame <white|black> <game number>";
        }

        var playerColor = params[0].toUpperCase();
        var index = Integer.parseInt(params[1]);

        Integer gameID = gameIndexMap.get(index);
        if (gameID == null) {
            return "Invalid game number.";
        }
        Game selectedGame = null;
        for (Game g : games) {
            if (g.gameID() == gameID) {
                selectedGame = g;
                break;
            }
        }

        if (selectedGame == null) {
            return "Game not found.";
        }

        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new ResponseException(400, "Error: Must be white or black");
        }

        int gameId = gameIndexMap.get(index);
        server.joinGame(playerColor, gameId);

        this.currentgameId = gameId;
        this.playerColor = playerColor;
        state = State.INGAME;
        this.webSocketFacade.connect(authToken, currentgameId, userName);

        return String.format("Joined game %d as player %s.", gameId, playerColor);
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
        } else if (state == State.OBSERVING) {
            return """
                    Available commands:
                    redrawboard - to redraw the chess board
                    leave - to leave the game
                    highlightmoves - to highlight all legal moves
                    help - to help with possible commands
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


    public void handleServerMessage(String message) {

        try {
            System.out.println();
            switch (new Gson().fromJson(message, ServerMessage.class).getServerMessageType()) {
                case LOAD_GAME:
                    this.currentGame = new Gson().fromJson(message, LoadGameMessage.class).getGame();
                    this.redrawBoard();
                    break;
                case ERROR:
                    System.out.println("Error: " + new Gson().fromJson(message, ErrorMessage.class).getErrorMessage());
                    break;
                case NOTIFICATION:
                    System.out.println("Notification: " + new Gson().fromJson(message, NotificationMessage.class).getNotificationMessage());
                    break;
                default:
                    System.out.println("Unknown message received in handleServerMessage in ChessClient.");
            }
        } catch (Exception e) {
            System.out.println("something went wrong in handle server message in chess client: " + e.getMessage());
        }
        printPrompt();

    }
}