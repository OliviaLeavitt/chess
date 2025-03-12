package dataaccess;

import dataaccess.mysql.MySQLUserDAO;
import exception.ResponseException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws ResponseException {
        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    @Test
    void createValidUser() throws ResponseException {
        User user = new User("testUsername", "testPassword", "testemail@gmail.com");
        User newUser = userDAO.createUser(user);
        assertNotNull(newUser);
        assertEquals("testUsername", newUser.username());
        assertEquals("testPassword", newUser.password());
        assertEquals("testemail@gmail.com", newUser.email());
    }

    @Test
    void getValidUser() throws ResponseException {
        User user1 = new User("testUsername1", "testPassword1", "testemail1@gmail.com");
        User user2 = new User("testUsername2", "testPassword2", "testemail2@gmail.com");

        User newUser1 = userDAO.createUser(user1);
        User newUser2 = userDAO.createUser(user2);

        assertNotNull(userDAO.getUser("testUsername1"));

        assertNotNull(userDAO.getUser("testUsername2"));
    }

    @Test
    void createDuplicateUser() throws ResponseException {
        User user = new User("duplicateUser", "password123", "email@example.com");
        userDAO.createUser(user);

        try {
            userDAO.createUser(user);
            fail("Should have failed for duplicate user");
        } catch (ResponseException e) {
            System.out.println("Can't create duplicate user");
        }
    }


    @Test
    void getNullUser() throws ResponseException {
        User retrievedUser = userDAO.getUser("userThatDoesNotExist");
        assertNull(retrievedUser);
    }

    @Test
    void clear() throws ResponseException {
        User user1 = new User("testUsername1", "testPassword1", "testemail1@gmail.com");
        User user2 = new User("testUsername2", "testPassword2", "testemail2@gmail.com");

        userDAO.createUser(user1);
        userDAO.createUser(user2);

        userDAO.clear();

        assertNull(userDAO.getUser("user1"));
        assertNull(userDAO.getUser("user2"));
    }



}
