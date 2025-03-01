package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.User;
import service.ClearService;
import service.RegisterService;
import service.results.RegisterResult;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clear);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerUser(Request req, Response res) throws ResponseException {
        try {
            var user = new Gson().fromJson(req.body(), User.class);
            RegisterResult registerResult = RegisterService.register(user);
            res.status(200);
            return new Gson().toJson(registerResult);

        } catch (ResponseException exception) {
            res.status(exception.StatusCode());
            return exception.toJson();
        } catch (Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }
    }

    private Object clear(Request req, Response res) throws ResponseException {
        try {
            ClearService.clear();
            res.status(200);
            return "";
        } catch (Exception exception) {
            res.status(500);
            return new ResponseException(500, "Error: " + exception.getMessage()).toJson();
        }
    }


}