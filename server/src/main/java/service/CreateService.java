package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.Auth;
import model.Game;
import results.CreateResult;

public class CreateService {
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public CreateService(AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;

    }

    public CreateResult createGame(String authToken, Game gameName) throws ResponseException {
        Auth authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (gameName == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        Game createdGame = gameDataAccess.createGame(gameName);
        int gameID = createdGame.gameID();

        return new CreateResult(gameID);
    }
}
