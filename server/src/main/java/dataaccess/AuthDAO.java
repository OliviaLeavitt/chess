package dataaccess;

import exception.ResponseException;
import model.Auth;

public interface AuthDAO {
    void clear() throws ResponseException;
    Auth createAuth(Auth auth) throws ResponseException;
    Auth getAuth(String authToken) throws ResponseException;
    void deleteAuth(String authToken) throws ResponseException;
}
