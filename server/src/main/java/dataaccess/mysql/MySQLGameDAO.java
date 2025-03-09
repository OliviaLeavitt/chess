package dataaccess.mysql;

import dataaccess.GameDAO;
import exception.ResponseException;
import model.Game;

import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    @Override
    public void clear() throws ResponseException {

    }

    @Override
    public Game createGame(Game game) throws ResponseException {
        return null;
    }

    @Override
    public Game getGame(int gameID) throws ResponseException {
        return null;
    }

    @Override
    public Collection<Game> listGames() throws ResponseException {
        return List.of();
    }

    @Override
    public void updateGame(Game game) throws ResponseException {

    }
}
