package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import exception.ResponseException;
import model.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private final MySQLUserDAO userDAO = new MySQLUserDAO();
    private final MySQLAuthDAO authDAO = new MySQLAuthDAO();
    private final MySQLGameDAO gameDAO = new MySQLGameDAO();
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
