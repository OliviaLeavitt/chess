package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.Game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO extends MySQLParentDAO implements GameDAO {
    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public Game createGame(Game game) throws ResponseException {
        String statement = "INSERT INTO games (gameName, whiteUsername, blackUsername, gameJson, gameOver) VALUES (?, ?, ?, ?, ?)";
        String gameJson = new Gson().toJson(game.game());
        int gameID = executeUpdate(statement, game.gameName(), game.whiteUsername(), game.blackUsername(), gameJson, 0);
        return getGame(gameID);
    }

    @Override
    public Game getGame(int gameID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private Game readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJson = rs.getString("gameJson");
        Boolean gameOver = rs.getBoolean("gameOver");
        var game = new Gson().fromJson(gameJson, ChessGame.class);
        return new Game(gameID, whiteUsername, blackUsername, gameName, game, gameOver);
    }

    @Override
    public Collection<Game> listGames() throws ResponseException {
        var result = new ArrayList<Game>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void updateGame(Game game) throws ResponseException {
        String statement = "UPDATE games SET gameName = ?, whiteUsername = ?, blackUsername = ?, gameJson = ?, gameOver = ? WHERE gameID = ?";
        String gameJson = new Gson().toJson(game.game());
        executeUpdate(statement, game.gameName(), game.whiteUsername(), game.blackUsername(), gameJson, game.gameOver() ? 1 : 0, game.gameID());
    }

}
