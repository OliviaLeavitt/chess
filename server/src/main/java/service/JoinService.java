//package service;
//
//import dataaccess.AuthDAO;
//import dataaccess.GameDAO;
//import dataaccess.UserDAO;
//import model.Auth;
//import model.Game;
//
//public class JoinService {
//    private final AuthDAO authDataAccess;
//    private final GameDAO gameDataAccess;
//
//    public JoinService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
//        this.authDataAccess = authDataAccess;
//        this.gameDataAccess = gameDataAccess;
//
//    }
//
//    public void joinGame(String authToken, String playerColor, int gameId) throws ResponseException {
//        Auth authData = authDataAccess.getAuth(authToken);
//        if (authData == null) {
//            throw new ResponseException(401, "Error: unauthorized");
//        }
//        Game gameData = gameDataAccess.getGame(gameId);
//        gameDataAccess.updateGame(gameData);
//    }
//}