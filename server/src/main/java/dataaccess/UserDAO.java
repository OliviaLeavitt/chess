package dataaccess;

import exception.ResponseException;
import model.User;

public interface UserDAO {
    void clear() throws ResponseException;
    User createUser(User user) throws ResponseException;
    User getUser(String username) throws ResponseException;
}

