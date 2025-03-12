package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import exception.ResponseException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    private final MySQLUserDAO userDAO = new MySQLUserDAO();
    private final MySQLAuthDAO authDAO = new MySQLAuthDAO();
    private final MySQLGameDAO gameDAO = new MySQLGameDAO();
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private final RegisterService registerService = new RegisterService(userDAO, authDAO);

    @BeforeEach
    void startFresh() throws ResponseException {
        clearService.clear();
    }
    @Test
    void registerSuccess() throws ResponseException {
        User user = new User("testUsername", "testPassword", "test@example.com");
        RegisterResult registerResult = registerService.register(user);
        assertNotNull(registerResult);
        assertEquals("testUsername", registerResult.username());
        assertNotNull(registerResult.authToken());
        assertNotNull(userDAO.getUser("testUsername"));
    }
    @Test
    void registerNoUsername() {
        User user = new User(null, "testPassword", "test@gmail.com");
        try {
            registerService.register(user);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(400, exception.statusCode());
        }
    }
    @Test
    void registerNoPassword() {
        User user = new User("testUsername", null, "test@gmail.com");
        try {
            registerService.register(user);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(400, exception.statusCode());
        }
    }
    @Test
    void registerNoEmail() {
        User user = new User("testUsername", "testPassword", null);
        try {
            registerService.register(user);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(400, exception.statusCode());
        }
    }
    @Test
    void registerAlreadyTaken() throws ResponseException {
        User priorUser = new User("testUsername", "priorPassword", "prior@gmail.com");
        userDAO.createUser(priorUser);

        User newUser = new User("testUsername", "testPassword", "test@example.com");
        try {
            registerService.register(newUser);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(403, exception.statusCode());
        }
    }

}
