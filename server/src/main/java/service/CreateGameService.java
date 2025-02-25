package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.Auth;
import model.User;

public class CreateGameService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public CreateGameService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;

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
