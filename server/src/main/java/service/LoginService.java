package service;


import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.Auth;
import model.User;

public class LoginService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public LoginService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public LoginResult login(User user, Auth auth) throws ResponseException {
        User userData = userDataAccess.getUser(user.username());
        //verify user data and password
        authDataAccess.createAuth(auth);
        return new LoginResult(user.username(), auth.authToken());
    }
}
