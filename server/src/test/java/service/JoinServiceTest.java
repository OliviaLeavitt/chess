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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JoinServiceTest {
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
    void joinSuccessGame() throws ResponseException {
        String authToken = "testAuthToken";
        String username = "testUser";
        authDAO.createAuth(new Auth(authToken, username));

        Game gameData = new Game(1, "whiteUsername", "blackUsername", "TestGame", new chess.ChessGame());
        CreateResult gameResult = createService.createGame(authToken, gameData);
        assertNotNull(gameResult);

        Game createdGame = gameDAO.getGame(gameResult.gameID());
        assertNotNull(createdGame);
        assertEquals("TestGame", createdGame.gameName());


    }
}
