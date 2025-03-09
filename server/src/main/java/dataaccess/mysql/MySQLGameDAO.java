package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.Game;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLGameDAO implements GameDAO {
    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public Game createGame(Game game) throws ResponseException {
        String statement = "INSERT INTO games (gameName, whiteUsername, blackUsername, gameData) VALUES (?, ?, ?, ?)";
        int gameID = executeUpdate(statement, game.gameName(), game.whiteUsername(), game.blackUsername(), game.game());
        return getGame(gameID);
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
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` VARCHAR(255) UNIQUE NOT NULL PRIMARY KEY,
            `gameName` VARCHAR(255) NOT NULL,
            `whiteUsername` VARCHAR(255) DEFAULT NULL,
            `blackUsername` VARCHAR(255) DEFAULT NULL,
            `gameData` TEXT NOT NULL,
            )
            """
    };
    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else {
                        if (param == null) ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
