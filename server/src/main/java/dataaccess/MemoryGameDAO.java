package dataaccess;

import model.Game;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextId = 1;
    final private HashMap<Integer, Game> games = new HashMap<>();

    public void clear() {
        games.clear();
        nextId = 1;
    }

    public Game createGame(Game game) {
        game = new Game(nextId++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), game.gameOver());
        games.put(game.gameID(), game);
        return game;
    }

    public Game getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<Game> listGames() {
        return games.values();
    }

    public void updateGame(Game game) {
        games.put(game.gameID(), game);
    }


}
