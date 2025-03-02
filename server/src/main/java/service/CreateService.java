package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.Auth;
import model.Game;

public class CreateService {
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public CreateService(AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;

    }

    public Game createGame(String authToken, Game gameName) throws ResponseException {
        Auth authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (gameName == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        return gameDataAccess.createGame(gameName);
    }
}
