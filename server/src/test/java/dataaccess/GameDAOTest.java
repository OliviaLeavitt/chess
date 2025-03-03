package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() throws ResponseException {
        gameDAO = new MemoryGameDAO();
        gameDAO.clear();
    }

    @Test
    void createGames() throws ResponseException {
        Game game0 = new Game(0, "whiteUser", "blackUser", "testGame0", new ChessGame());
        Game game1 = new Game(0, "whiteUser1", "blackUser1", "testGame1", new ChessGame());
        Game createdGame0 = gameDAO.createGame(game0);
        Game createdGame1 = gameDAO.createGame(game1);

        assertNotNull(createdGame0);
        assertNotNull(createdGame1);

        assertEquals("whiteUser", createdGame0.whiteUsername());
        assertEquals("blackUser", createdGame0.blackUsername());
        assertEquals("testGame0", createdGame0.gameName());

        Game retrievedGame = gameDAO.getGame(createdGame0.gameID());
        assertNotNull(retrievedGame);
        assertEquals(createdGame0, retrievedGame);

        assertEquals("whiteUser1", createdGame1.whiteUsername());
        assertEquals("blackUser1", createdGame1.blackUsername());
        assertEquals("testGame1", createdGame1.gameName());

        Game retrievedGame1 = gameDAO.getGame(createdGame1.gameID());
        assertNotNull(retrievedGame1);
        assertEquals(createdGame1, retrievedGame1);
    }

    @Test
    void getGame() throws ResponseException {
        Game game0 = new Game(0, "white1", "black1", "game1", new ChessGame());
        Game game1 = new Game(0, "white2", "black2", "game2", new ChessGame());

        Game createdGame0 = gameDAO.createGame(game0);
        Game createdGame1 = gameDAO.createGame(game1);

        assertNotNull(gameDAO.getGame(createdGame0.gameID()));
        assertEquals(createdGame0, gameDAO.getGame(createdGame0.gameID()));

        assertNotNull(gameDAO.getGame(createdGame1.gameID()));
        assertEquals(createdGame1, gameDAO.getGame(createdGame1.gameID()));
    }

    @Test
    void getNullGame() throws ResponseException {
        Game retrievedGame = gameDAO.getGame(5);
        assertNull(retrievedGame);
    }

    @Test
    void clear() throws ResponseException {
        Game game0 = new Game(0, "white1", "black1", "game1", new ChessGame());
        Game game1 = new Game(0, "white2", "black2", "game2", new ChessGame());
        gameDAO.createGame(game0);
        gameDAO.createGame(game1);

        gameDAO.clear();

        Collection<Game> games = gameDAO.listGames();
        assertEquals(0, games.size());
    }

    @Test
    void listGames() throws ResponseException {
        Game game0 = new Game(0, "white1", "black1", "game1", new ChessGame());
        Game game1 = new Game(0, "white2", "black2", "game2", new ChessGame());
        Game game2 = new Game(0, "white3", "black3", "game3", new ChessGame());
        gameDAO.createGame(game0);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);

        Collection<Game> games = gameDAO.listGames();
        assertEquals(3, games.size());
        assertTrue(games.contains(gameDAO.getGame(1)));
        assertTrue(games.contains(gameDAO.getGame(2)));
        assertTrue(games.contains(gameDAO.getGame(3)));
    }

    @Test
    void updateGame() throws ResponseException {
        Game game = new Game(0, "white", "black", "game", new ChessGame());
        Game createdGame = gameDAO.createGame(game);

        Game updatedGame = new Game(createdGame.gameID(), "updatedWhite", "updatedBlack", "updatedName", new ChessGame());
        gameDAO.updateGame(updatedGame);

        assertNotNull(gameDAO.getGame(createdGame.gameID()));
        assertEquals(updatedGame, gameDAO.getGame(createdGame.gameID()));
    }



}
