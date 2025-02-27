package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import service.results.ClearResult;

public class ClearService {
    private static UserDAO userDataAccess = new MemoryUserDAO();
    private static AuthDAO authDataAccess = new MemoryAuthDAO();
    private static GameDAO gameDataAccess = new MemoryGameDAO();

    public ClearService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        ClearService.userDataAccess = userDataAccess;
        ClearService.authDataAccess = authDataAccess;
        ClearService.gameDataAccess = gameDataAccess;

    }

    public static ClearResult clear() throws ResponseException {
        userDataAccess.clear();
        authDataAccess.clear();
        gameDataAccess.clear();
        return new ClearResult();
    }
}
