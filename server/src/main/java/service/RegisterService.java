package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.Auth;
import model.User;

public class RegisterService {

    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public RegisterService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;

    }

    public RegisterResult register(User user, Auth auth) throws ResponseException {
        if (userDataAccess.getUser(user.username()) != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        userDataAccess.createUser(user);
        authDataAccess.createAuth(auth);
        return new RegisterResult(user.username(), auth.authToken());
    }

}
