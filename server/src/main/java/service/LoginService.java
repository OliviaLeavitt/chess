package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.Auth;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public LoginService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        User userData = userDataAccess.getUser(loginRequest.username());
        if (userData == null || !BCrypt.checkpw(loginRequest.password(), userData.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        Auth authData = new Auth(authToken, loginRequest.username());
        authDataAccess.createAuth(authData);

        return new LoginResult(loginRequest.username(), authToken);
    }
}