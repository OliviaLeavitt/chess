package server;

import model.Auth;
import model.Game;
import model.User;

import java.net.HttpURLConnection;
import com.google.gson.Gson;
import exception.ErrorResponse;
import exception.ResponseException;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public Auth register(User user) throws ResponseException {
        var path = "/user";
        record RegisterRequest(String username, String password, String email) {}
        var request = new RegisterRequest(user.username(), user.password(), user.email());
        return this.makeRequest("POST", path, request, Auth.class);
    }

    public LoginResult login(String username, String password) throws ResponseException {
        var path = "/session";
        record LoginRequest(String username, String password) {}
        var request = new LoginRequest(username, password);
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null);
    }

    public CreateResult createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        var request = new CreateRequest(authToken, gameName);
        return this.makeRequest("POST", path, request, CreateResult.class);
    }

    public Game[] listGames(String authToken) throws ResponseException {
        var path = "/game";
        record listGamesResponse(Game[] games) {}
        var response = this.makeRequest("GET", path, null, listGamesResponse.class);
        return response.games();
    }

    public void joinGame(String authToken, String playerColor, int gameId) throws ResponseException {
        var path = "/game";
        record JoinGameRequest(String authToken, String playerColor, int gameId) {}
        var request = new JoinGameRequest(authToken, playerColor, gameId);
        this.makeRequest("PUT", path, request, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}


