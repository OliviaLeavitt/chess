package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import exception.ResponseException;
import model.Game;
import model.User;
import requests.CreateRequest;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.RegisterResult;
import server.websocket.WebSocketHandler;
import service.*;
import requests.LoginRequest;
import requests.JoinRequest;
import spark.*;

public class Server {
    private final UserDAO userDAO = new MySQLUserDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final WebSocketHandler webSocketHandler;

    public Server() {
        this.webSocketHandler = new WebSocketHandler(gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);


        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", webSocketHandler);

        try {
            DatabaseManager.configureDatabase();
        } catch (ResponseException | DataAccessException e) {
            System.out.println("exception" + e.getMessage());
        }

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);


        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object joinGame(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
            JoinService joinService = new JoinService(authDAO, gameDAO);
            joinService.joinGame(authToken, joinRequest.playerColor(), joinRequest.gameID());
            res.status(200);
            return "";
        } catch (ResponseException exception) {
            res.status(exception.statusCode());
            return exception.toJson();
        } catch(Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }
    }

    private Object createGame(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            CreateRequest createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
            Game game = new Game(100, null, null, createRequest.gameName(), new ChessGame(), false);
            CreateService createService = new CreateService(authDAO, gameDAO);
            CreateResult gameResult = createService.createGame(authToken, game);
            res.status(200);
            return new Gson().toJson(gameResult);
        } catch (ResponseException exception) {
            res.status(exception.statusCode());
            return exception.toJson();
        } catch (Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }
    }

    private Object listGames(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            ListService listService = new ListService(authDAO, gameDAO);
            ListResult listResult = listService.listGames(authToken);
            res.status(200);
            return new Gson().toJson(listResult);
        } catch (ResponseException exception) {
            res.status(exception.statusCode());
            return exception.toJson();
        } catch (Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }

    }

    private Object logoutUser(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            LogoutService logoutService = new LogoutService(authDAO);
            logoutService.logout(authToken);
            res.status(200);
            return "";
        } catch (ResponseException exception) {
            res.status(exception.statusCode());
            return exception.toJson();
        } catch (Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }
    }

    private Object loginUser(Request req, Response res) {
        try {
            LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginService loginService = new LoginService(userDAO, authDAO);
            LoginResult loginResult = loginService.login(loginRequest);
            res.status(200);
            return new Gson().toJson(loginResult);
        } catch (ResponseException exception) {
            res.status(exception.statusCode());
            return exception.toJson();
        } catch (Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerUser(Request req, Response res) {
        try {
            var user = new Gson().fromJson(req.body(), User.class);
            RegisterService registerService = new RegisterService(userDAO, authDAO);
            RegisterResult registerResult = registerService.register(user);
            res.status(200);
            return new Gson().toJson(registerResult);
        } catch (ResponseException exception) {
            res.status(exception.statusCode());
            return exception.toJson();
        } catch (Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }
    }

    private Object clear(Request req, Response res) {
        try {
            ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
            clearService.clear();
            res.status(200);
            return "";
        } catch (Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }
    }


}