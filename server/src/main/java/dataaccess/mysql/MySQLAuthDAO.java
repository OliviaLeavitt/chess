package dataaccess.mysql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import exception.ResponseException;
import model.Auth;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public Auth createAuth(Auth auth) throws ResponseException {
        var statement = "INSERT INTO users (authToken, use) VALUES (?, ?, ?)";
        executeUpdate(statement, auth.authToken(), auth.username());
        return new Auth(auth.authToken(), auth.username());
    }

    @Override
    public Auth getAuth(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private Auth readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new Auth(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {

    }

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
}
