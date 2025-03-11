package dataaccess.mysql;

import dataaccess.AuthDAO;
import exception.ResponseException;
import model.Auth;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public Auth createAuth(Auth auth) throws ResponseException {
        return null;
    }

    @Override
    public Auth getAuth(String authToken) throws ResponseException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {

    }
}
