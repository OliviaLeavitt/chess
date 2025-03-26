package client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import chess.ChessGame;
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
    private State state = State.PRELOGIN;

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
            } else {
                throw new IllegalStateException("Invalid client state: " + state);
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
            CreateResult result = server.createGame(gameName);
            return String.format("Created game: %s (with game id: %d)", gameName, result.gameID());
        }
        throw new ResponseException(400, "Expected: <gameName>");
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

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 3) {
            var playerColor = params[0].toUpperCase();
            var gameId = Integer.parseInt(params[1]);
            if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                throw new ResponseException(400, "Color must be white or black");
            }
            server.joinGame(playerColor, gameId);
            return String.format("Joined game %d as player %s.", gameId, playerColor);
        }
        throw new ResponseException(400, "Expected: <gameID> <WHITE|BLACK>");
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
            DrawChessBoard.drawChessboard(null, playerColor);
            return String.format("Joined game %d as player %s.  Displaying initial board state.", gameId, playerColor);
        }
        throw new ResponseException(400, "Error: Expected: playgame <WHITE|BLACK> <game number>");
    }

    public String help() {
        if (state == State.PRELOGIN) {
            return """
                    Available commands:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                Available commands:
                logout - log out of your account
                creategame <gameName> - create a new game
                listgames - list available games
                playgame <WHITE|BLACK> <game number> - join a game as a player
                observegame <game number> - observe a game
                help - display available commands
                quit - exit the program
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.PRELOGIN) {
            throw new ResponseException(400, "You must log in");
        }
    }
}