package client;

import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import exception.ResponseException;
import model.User;
import org.junit.jupiter.api.*;
import results.LoginResult;
import server.Server;
import server.ServerFacade;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int port;
    private final MySQLUserDAO userDAO = new MySQLUserDAO();
    private final MySQLAuthDAO authDAO = new MySQLAuthDAO();
    private final MySQLGameDAO gameDAO = new MySQLGameDAO();
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        clearService.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerSuccess() throws Exception {
        User testUser = new User("testUser", "testPassword", "test@gmail.com");
        var auth = facade.register(testUser);
        assertNotNull(auth.authToken());
        assertNotNull(auth.username());
        assertEquals("testUser", auth.username());
    }

    @Test
    void registerFailDuplicate() throws ResponseException {
        User testUser = new User("duplicateUser", "testPassword", "duplicate@gmail.com");
        facade.register(testUser);

        try {
            facade.register(testUser);
            fail("Expected ResponseException for duplicate username");
        } catch (ResponseException e) {
            System.out.println("Can't have duplicates");
        }
    }

    @Test
    void loginSuccess() throws ResponseException {
        User testUser = new User("loginTestUser", "testPassword", "test@gmail.com");
        facade.register(testUser);

        LoginResult loginResult = facade.login("loginTestUser", "testPassword");
        assertNotNull(loginResult.authToken());
        assertEquals("loginTestUser", loginResult.username());
    }

    @Test
    void loginFailWrongPassword() throws ResponseException {
        try {
            User testUser = new User("wrongPassUser", "testPassword", "test@gmail.com");
            facade.register(testUser);

            facade.login("wrongPassUser", "wrongPassword");
            fail("Should have failed for wrong password");
        } catch (ResponseException e) {
            System.out.println("Bad password");
        }
    }
    @Test
    void logoutSuccess() throws ResponseException {
        User testUser = new User("logoutUser", "testPassword", "logout@gmail.com");
        var auth = facade.register(testUser);

        try {
            facade.logout(auth.authToken());
        } catch (ResponseException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

}
