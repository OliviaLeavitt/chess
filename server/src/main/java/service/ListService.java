//package service;
//
//import dataaccess.AuthDAO;
//import dataaccess.GameDAO;
//import dataaccess.UserDAO;
//import model.Auth;
//import model.Game;
//import model.User;
//import service.results.ListResult;
//
//import java.util.Collection;
//
//public class ListService {
//    private final UserDAO userDataAccess;
//    private final AuthDAO authDataAccess;
//    private final GameDAO gameDataAccess;
//
//    public ListService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
//        this.userDataAccess = userDataAccess;
//        this.authDataAccess = authDataAccess;
//        this.gameDataAccess = gameDataAccess;
//    }
//
//    public Collection<Game> listGames(String authToken) throws ResponseException {
//
//        Auth authData = authDataAccess.getAuth(authToken);
//        if (authData == null) {
//            throw new ResponseException(401, "Error: unauthorized");
//        }
//
//        gameDataAccess.listGames();
//        return new ListResult()
//    }
//}
