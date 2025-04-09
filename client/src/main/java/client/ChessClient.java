package client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import chess.*;
import com.google.gson.Gson;
import exception.ResponseException;
import model.Game;
import model.User;
import results.CreateResult;
import results.LoginResult;
import server.ServerFacade;
import ui.DrawChessBoard;


public class ChessClient {
    private final ServerFacade server;
    private String authToken = null;
    private final String serverUrl;
    private ChessGame currentGame;
    private int currentgameId = 0;
    private State state = State.PRELOGIN;
    private final Gson gson = new Gson();

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
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
//                    case "leave" -> leaveGame();
//                    case "resign" -> resign();
//                    case "highlightmoves" -> highlightLegalMoves();
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

//    private String highlightLegalMoves() {
//    }
//
//    private String resign() {
//    }
//
//    private String leaveGame() {
//    }

    public void setCurrentGame(ChessGame game) {
        this.currentGame = game;
    }

    private String makeMove() {
        Scanner makeMoveScanner = new Scanner(System.in);
        System.out.print("Enter your move (ex: a2a3). If promoting, add the piece name (ex: a7a8 queen): ");
        String userMoveInput = makeMoveScanner.nextLine();

        String[] userInputArray = userMoveInput.split(" ");
        if (userInputArray[0].length() != 4) {
            return "Invalid format: Please enter the start and end squares (ex: a2a3). If promoting, add the new piece (ex: a7a8 queen).";
        }
        if (userInputArray[0].charAt(0) < 'a' || userInputArray[0].charAt(0) > 'h' ||
                userInputArray[0].charAt(2) < 'a' || userInputArray[0].charAt(2) > 'h') {
            return "Invalid column. Please use columns a-h for both the start and end positions.";
        }
        if (userInputArray[0].charAt(1) < '1' || userInputArray[0].charAt(1) > '8' ||
                userInputArray[0].charAt(3) < '1' || userInputArray[0].charAt(3) > '8') {
            return "Invalid row. Please use rows 1-8 for both the start and end positions.";
        }

        try {
            int startCol = userInputArray[0].charAt(0) - 'a' + 1;
            int startRow = Character.getNumericValue(userInputArray[0].charAt(1));

            int endCol = userInputArray[0].charAt(2) - 'a' + 1;
            int endRow = Character.getNumericValue(userInputArray[0].charAt(3));

            ChessPosition startPosition = new ChessPosition(startRow, startCol);
            ChessPosition endPosition = new ChessPosition(endRow, endCol);
            ChessBoard chessBoard = currentGame.getBoard();

            ChessPiece movingPiece = chessBoard.getPiece(startPosition);
            if (movingPiece == null) {
                return "No piece at the start position. Choose a valid piece to move.";
            }

            ChessPiece.PieceType promotionPiece = null;
            if (userInputArray.length == 2) {
                try {
                    promotionPiece = ChessPiece.PieceType.valueOf(userInputArray[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    return "Invalid promotion piece! Choose from: KING, QUEEN, BISHOP, KNIGHT, ROOK, or PAWN.";
                }
            } else if (userInputArray.length > 2) {
                return "You entered an invalid number of arguments. Only enter your move and promotion piece, if applicable.";
            }

            ChessMove move = new ChessMove(endPosition, startPosition, promotionPiece);
            Collection<ChessMove> validMoves = movingPiece.pieceMoves(currentGame.getBoard(), startPosition);
            if (!validMoves.contains(move)) {
                return "That move is invalid.";
            }


            currentGame.makeMove(move);
            return "Move executed successfully.";
        } catch (InvalidMoveException e) {
            return "Error: The move could not be executed. It may be illegal or cause an invalid board state. " +
                    "Please check your move and try again.";
        }
    }


        //get input to move piece to
        //create the move from input
        // send that move back to serverfacade
        // chessclient (creates move) -> serverfacade -> server -> service -> daos -> database



    private String redrawBoard() {
        String currentTurn = currentGame.getTeamTurn().toString();
        DrawChessBoard.drawChessboard(currentGame, currentTurn);
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
            var gameName = params[0];
            CreateResult gameResult = server.createGame(gameName);
            int gameId = gameResult.gameID();
            Game game = getGameFromId(gameId);
            if (game != null) {
                this.currentGame = game.game();
                this.currentgameId = gameId;
                return String.format("Created game: %s (with game id: %d)", gameName, gameId);
            } else {
                return String.format("Created game: %s (with game id: %d), but failed to retrieve game details.", gameName, gameId);
            }
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }


    public Game getGameFromId(int gameID) throws ResponseException {
        assertSignedIn();
        return server.getGame(gameID);
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        var games = server.listGames(authToken);
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
            DrawChessBoard.drawChessboard(currentGame, "WHITE");
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
            DrawChessBoard.drawChessboard(null, playerColor);
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
}