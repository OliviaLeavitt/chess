package service;

import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import exception.ResponseException;
import model.Auth;
import model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.ListResult;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ListServiceTest {
    private final MySQLUserDAO userDAO = new MySQLUserDAO();
    private final MySQLAuthDAO authDAO = new MySQLAuthDAO();
    private final MySQLGameDAO gameDAO = new MySQLGameDAO();
    private final ListService listService = new ListService(authDAO, gameDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private final CreateService createService = new CreateService(authDAO, gameDAO);

    @BeforeEach
    void startFresh() throws ResponseException {
        clearService.clear();
    }

    @Test
    void listSuccessGames() throws ResponseException {
        String authToken = "testAuthToken";
        String username = "testUsername";
        authDAO.createAuth(new Auth(authToken, username));

        Game gameData = new Game(1, "whiteUsername", "blackUsername", "TestGame", new chess.ChessGame());
        Game gameData2 = new Game(2, "whiteUsername2", "blackUsername2", "TestGame2", new chess.ChessGame());
        Game gameData3 = new Game(3, "whiteUsername3", "blackUsername3", "TestGame3", new chess.ChessGame());
        createService.createGame(authToken, gameData);
        createService.createGame(authToken, gameData2);
        createService.createGame(authToken, gameData3);

        ListResult listResult = listService.listGames(authToken);
        assertNotNull(listResult);

        Collection<Game> games = listResult.games();
        assertNotNull(games);
        assertEquals(3, games.size());
    }
    @Test
    void listUnauthorizedGames() {
        try {
            listService.listGames("invalidAuthToken");
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(401, exception.statusCode());
        }
    }
    @Test
    void listEmptyGames() throws ResponseException {
        String authToken = "testAuthToken";
        String username = "testUsername";
        authDAO.createAuth(new Auth(authToken, username));

        ListResult listResult = listService.listGames(authToken);
        assertNotNull(listResult);

        Collection<Game> games = listResult.games();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

}
