package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

  private GameDAO gameDAO;
  private GameService gameService;

  @BeforeEach
  public void setUp() {
    gameDAO = new GameDAO();
    gameService = new GameService(gameDAO);
  }

  @Test
  public void testCreateGameSuccess() throws DataAccessException {
    GameData game = gameService.createGame("Chess Match 1", "john");
    assertNotNull(game);
    assertEquals("Chess Match 1", game.gameName());
    assertEquals("john", game.whiteUsername());
  }

  @Test
  public void testListGames() throws DataAccessException {
    gameService.createGame("Chess Match 1", "john");
    gameService.createGame("Chess Match 2", "jane");

    var games = gameService.listGames();
    assertEquals(2, games.size());
  }
}
