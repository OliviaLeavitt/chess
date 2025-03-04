package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.Auth;
import model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.results.CreateResult;

import static org.junit.jupiter.api.Assertions.*;

public class CreateServiceTest {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final CreateService createService = new CreateService(authDAO, gameDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    @BeforeEach
    void startFresh() throws ResponseException {
        clearService.clear();
    }

    @Test
    void createSuccessGame() throws ResponseException {
        String authToken = "testAuthToken";
        String username = "testUsername";
        authDAO.createAuth(new Auth(authToken, username));

        Game gameData = new Game(1, "whiteUsername", "blackUsername", "TestGame", new chess.ChessGame());
        CreateResult gameResult = createService.createGame(authToken, gameData);
        assertNotNull(gameResult);

        Game createdGame = gameDAO.getGame(gameResult.gameID());
        assertNotNull(createdGame);
        assertEquals("TestGame", createdGame.gameName());
    }
    @Test
    void createUnauthorizedGame() {
        String trueAuthToken = "trueAuthToken";
        String username = "testUsername";
        authDAO.createAuth(new Auth(trueAuthToken, username));

        Game gameData = new Game(1, "whiteUsername", "blackUsername", "TestGame", new chess.ChessGame());
        String invalidAuthToken = "invalidAuthToken";
        try {
            createService.createGame(invalidAuthToken, gameData);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(401, exception.statusCode());
        }
    }
    @Test
    void createBadRequestGame() {
        String authToken = "testAuthToken";
        String username = "testUsername";
        authDAO.createAuth(new Auth(authToken, username));

        try {
            createService.createGame(authToken, null);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(400, exception.statusCode());
        }
    }

}
