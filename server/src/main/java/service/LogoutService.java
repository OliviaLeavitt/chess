package service;

import dataaccess.AuthDAO;
import exception.ResponseException;
import model.Auth;

public class LogoutService {
    private final AuthDAO authDataAccess;

    public LogoutService(AuthDAO authDataAccess) {
        this.authDataAccess = authDataAccess;
    }

    public void logout(String authToken) throws ResponseException {
        Auth authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        authDataAccess.deleteAuth(authToken);
    }
}
