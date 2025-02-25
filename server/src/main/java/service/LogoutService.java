package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.Auth;
import model.User;

public class LogoutService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public LogoutService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public void logout(User user, Auth auth) throws ResponseException {

        Auth authData = authDataAccess.getAuth(auth.authToken());
        authDataAccess.deleteAuth(auth.authToken());
    }
}
