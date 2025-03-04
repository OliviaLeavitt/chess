package service;

import dataaccess.*;
import exception.ResponseException;
import model.Auth;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ClearServiceTest {

    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    @BeforeEach
    void startFresh() throws ResponseException {
        clearService.clear();
    }

    @Test
    void clear() throws ResponseException {
        userDAO.createUser(new User("testUsername", "testPassword", "test@gmail.com"));
        authDAO.createAuth(new Auth("testAuthToken", "testUsername"));
        gameDAO.createGame(new model.Game(1, "whiteUser", "blackUser", "TestGameName", new chess.ChessGame()));

        clearService.clear();

        assertNull(userDAO.getUser("testUsername"));
        assertNull(authDAO.getAuth("testAuthToken"));
        assertNull(gameDAO.getGame(1));
    }
}
