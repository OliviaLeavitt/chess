package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.Auth;
import model.Game;
import service.results.ListResult;

import java.util.Collection;

public class ListService {
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public ListService(AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public ListResult listGames(String authToken) throws ResponseException {
        Auth authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        Collection<Game> games = gameDataAccess.listGames();

        return new ListResult(games);
    }
}
