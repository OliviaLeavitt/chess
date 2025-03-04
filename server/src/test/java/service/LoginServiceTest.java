package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.LoginRequest;
import service.results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private final LoginService loginService = new LoginService(userDAO, authDAO);

    @BeforeEach
    void startFresh() throws ResponseException {
        clearService.clear();
    }

    @Test
    void loginSuccess() throws ResponseException {
        String username = "testUser";
        String password = "testPassword";
        User user = new User(username, password, "test@gmail.com");
        userDAO.createUser(user);

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult loginResult = loginService.login(loginRequest);

        assertNotNull(loginResult);
        assertNotNull(loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    void loginUnauthorizedPassword() {
        String username = "testUser";
        String password = "testPassword";
        User user = new User(username, password, "test@gmail.com");
        userDAO.createUser(user);

        LoginRequest loginRequest = new LoginRequest(username, "invalidPassword");
        try {
            loginService.login(loginRequest);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(401, exception.statusCode());
        }
    }
    @Test
    void loginNoUserData() {
        LoginRequest loginRequest = new LoginRequest(null, null);
        try {
            loginService.login(loginRequest);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(401, exception.statusCode());
        }
    }
}

