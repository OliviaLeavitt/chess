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

public class JoinServiceTest {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final CreateService createService = new CreateService(authDAO, gameDAO);
    private final JoinService joinService = new JoinService(authDAO, gameDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    @BeforeEach
    void startFresh() throws ResponseException {
        clearService.clear();
    }
    @Test
    void joinSuccessGame() throws ResponseException {
        String authToken = "testAuthToken";
        String username = "testUser";
        authDAO.createAuth(new Auth(authToken, username));

        Game gameData = new Game(1, null, "blackUsername", "TestGame", new chess.ChessGame());
        CreateResult gameResult = createService.createGame(authToken, gameData);
        assertNotNull(gameResult);

        Game createdGame = gameDAO.getGame(gameResult.gameID());
        assertNotNull(createdGame);
        assertEquals("TestGame", createdGame.gameName());

        joinService.joinGame(authToken, "WHITE", createdGame.gameID());

        Game updatedGame = gameDAO.getGame(gameResult.gameID());
        assertEquals(username, updatedGame.whiteUsername());

    }
    @Test
    void joinUnauthorizedGame() {
        String authToken = "testAuthToken";
        try {
            joinService.joinGame(authToken, "WHITE", 1);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(401, exception.statusCode());
        }
    }
    @Test
    void joinNoGameDataGame() {
        String authToken = "testAuthToken";
        String username = "testUser";
        authDAO.createAuth(new Auth(authToken, username));
        try {
            joinService.joinGame(authToken, "WHITE", 1);
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(400, exception.statusCode());
        }
    }
    @Test
    void joinNoPlayerColorGame() throws ResponseException {
        String authToken = "testAuthToken";
        String username = "testUser";
        authDAO.createAuth(new Auth(authToken, username));

        Game gameData = new Game(1, null, "blackUsername", "TestGame", new chess.ChessGame());
        CreateResult gameResult = createService.createGame(authToken, gameData);
        assertNotNull(gameResult);

        try {
            joinService.joinGame(authToken, null, gameResult.gameID());
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(400, exception.statusCode());
        }
    }
    @Test
    void joinAlreadyTakenGame() throws ResponseException {
        String authToken = "testAuthToken";
        String username = "testUser";
        authDAO.createAuth(new Auth(authToken, username));

        Game gameData = new Game(1, "whiteUsername", "blackUsername", "TestGame", new chess.ChessGame());
        CreateResult gameResult = createService.createGame(authToken, gameData);
        assertNotNull(gameResult);

        try {
            joinService.joinGame(authToken, "WHITE", gameResult.gameID());
            fail("Exception should have been thrown");
        } catch (ResponseException exception) {
            assertEquals(403, exception.statusCode());
        }
    }

}
