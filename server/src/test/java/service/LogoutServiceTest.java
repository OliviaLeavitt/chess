package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private final LogoutService logoutService = new LogoutService(authDAO);

    @BeforeEach
    void startFresh() throws ResponseException {
        clearService.clear();
    }
    @Test
    void logoutSuccess() throws ResponseException {
        String authToken = "testAuthToken";
        String username = "testUsername";
        Auth auth = new Auth(authToken, username);
        authDAO.createAuth(auth);

        logoutService.logout(authToken);

        assertNull(authDAO.getAuth(authToken));
    }
    @Test
    void logoutUnauthorized() {
        try {
            logoutService.logout("invalidAuthToken");
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(401, exception.statusCode());
        }
    }
}
