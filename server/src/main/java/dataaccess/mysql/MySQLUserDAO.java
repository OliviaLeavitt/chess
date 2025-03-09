package dataaccess.mysql;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.User;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLUserDAO implements UserDAO {
    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    @Override
    public User createUser(User user) throws ResponseException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        var json = new Gson().toJson(user);
        executeUpdate(statement, user.username(), user.password(), user.email(), json);
        return new User(user.username(), user.password(), user.email());
    }

    @Override
    public User getUser(String username) throws ResponseException {
        return null;
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
            `username` VARCHAR(255) UNIQUE NOT NULL,
            `password` VARCHAR(255),
            `email` VARCHAR(255),
            PRIMARY KEY (`username`)
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