package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.Auth;
import model.User;
import service.results.RegisterResult;

import java.util.UUID;

public class RegisterService {

    private static UserDAO userDataAccess;
    private static AuthDAO authDataAccess;

    public RegisterService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        RegisterService.userDataAccess = userDataAccess;
        RegisterService.authDataAccess = authDataAccess;

    }

    public static RegisterResult register(User user) throws ResponseException {
        if (userDataAccess.getUser(user.username()) != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        userDataAccess.createUser(user);
        String authToken = UUID.randomUUID().toString();
        Auth authData = new Auth(authToken, user.username());
        authDataAccess.createAuth(authData);
        return new RegisterResult(user.username(), authToken);
    }

}
