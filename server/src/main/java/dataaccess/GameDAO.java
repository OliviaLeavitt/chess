package dataaccess;

import exception.ResponseException;
import model.Game;

import java.util.Collection;

public interface GameDAO {
    void clear() throws ResponseException;
    Game createGame(Game game) throws ResponseException;
    Game getGame(int gameID) throws ResponseException;
    Collection<Game> listGames() throws ResponseException;
    void updateGame(Game game) throws ResponseException;


}
