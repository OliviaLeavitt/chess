package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.Auth;
import model.Game;
import service.results.JoinResult;

public class JoinService {
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public JoinService(AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;

    }

    public void joinGame(String authToken, String playerColor, int gameId) throws ResponseException {
        Auth authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        Game gameData = gameDataAccess.getGame(gameId);
        if (gameData == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (playerColor == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        checkIfPlayerExists(playerColor, gameData);

        String username = authData.username();
        Game updatedGameData = updateGameData(gameData, playerColor, username);
        gameDataAccess.updateGame(updatedGameData);
    }

    private void checkIfPlayerExists(String playerColor, Game gameData) throws ResponseException {
        boolean playerIsWhite = playerColor.equals("WHITE");
        boolean whiteUserAlreadyExists = gameData.whiteUsername() != null;
        if (playerIsWhite && whiteUserAlreadyExists) {
            throw new ResponseException(403, "Error: already taken");
        }

        boolean playerIsBlack = playerColor.equals("BLACK");
        boolean blackUserAlreadyExists = gameData.blackUsername() != null;
        if (playerIsBlack && blackUserAlreadyExists) {
            throw new ResponseException(403, "Error: already taken");
        }
    }

    private Game updateGameData(Game gameData, String playerColor, String newUsername) throws ResponseException {
        if (playerColor.equals("WHITE")) {
            return new Game(gameData.gameID(), newUsername, gameData.blackUsername(), gameData.gameName(), gameData.game());
        }
        if (playerColor.equals("BLACK")) {
            return new Game(gameData.gameID(), gameData.whiteUsername(), newUsername, gameData.gameName(), gameData.game());
        } else {
            throw new ResponseException(400, "Error: bad request");
        }
    }
}