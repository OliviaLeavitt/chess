package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.Auth;
import model.User;
import service.results.RegisterResult;

import java.util.UUID;

public class RegisterService {

    private static UserDAO userDataAccess = new MemoryUserDAO();
    private static AuthDAO authDataAccess = new MemoryAuthDAO();

    public RegisterService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        RegisterService.userDataAccess = userDataAccess;
        RegisterService.authDataAccess = authDataAccess;

    }

    public static RegisterResult register(User user) throws ResponseException {
        System.out.println(userDataAccess.toString());
        System.out.println(user);


        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
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
