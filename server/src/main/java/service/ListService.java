package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.Auth;
import model.User;

public class ListService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public ListService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public ListResult listGames(User user, Auth auth) throws ResponseException {

        Auth authData = authDataAccess.getAuth(auth.authToken());
        if (authData != null) {
            gameDataAccess.listGames();
        }
        return new ListResult(); // how do i make this?
    }
}
