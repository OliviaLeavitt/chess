package dataaccess;

import dataaccess.mysql.MySQLAuthDAO;
import exception.ResponseException;
import model.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {
    private AuthDAO authDAO;

    @BeforeEach
    void setUp() throws ResponseException {
        authDAO = new MySQLAuthDAO();
        authDAO.clear();
    }

    @Test
    void createValidAuth() throws ResponseException {
        Auth auth = new Auth("testAuthToken", "testUsername");
        Auth newAuth = authDAO.createAuth(auth);

        assertNotNull(newAuth);
        assertEquals("testAuthToken", newAuth.authToken());
        assertEquals("testUsername", newAuth.username());

    }

    @Test
    void getValidAuth() throws ResponseException {
        Auth auth1 = new Auth("testAuthToken1", "testUsername1");
        Auth auth2 = new Auth("testAuthToken2", "testUsername2");

        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);

        assertNotNull(authDAO.getAuth("testAuthToken1"));
        assertEquals("testAuthToken1", authDAO.getAuth("testAuthToken1").authToken());
        assertEquals("testUsername1", authDAO.getAuth("testAuthToken1").username());

        assertNotNull(authDAO.getAuth("testAuthToken2"));
        assertEquals("testAuthToken2", authDAO.getAuth("testAuthToken2").authToken());
        assertEquals("testUsername2", authDAO.getAuth("testAuthToken2").username());

    }

    @Test
    void getNullAuth() throws ResponseException {
        Auth retrievedAuth = authDAO.getAuth("authTokenThatDoesNotExist");
        assertNull(retrievedAuth);
    }

    @Test
    void clearAuth() throws ResponseException {
        Auth auth1 = new Auth("testAuthToken1", "testUsername1");
        Auth auth2 = new Auth("testAuthToken2", "testUsername2");

        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);

        authDAO.clear();

        assertNull(authDAO.getAuth("testAuthToken1"));
        assertNull(authDAO.getAuth("testAuthToken2"));
    }

    @Test
    void deleteAuth() throws ResponseException {
        Auth auth1 = new Auth("testAuthToken1", "testUsername1");
        Auth auth2 = new Auth("testAuthToken2", "testUsername2");

        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);

        authDAO.deleteAuth("testAuthToken1");

        assertNull(authDAO.getAuth("testAuthToken1"));
        assertNotNull(authDAO.getAuth("testAuthToken2"));
    }
}
