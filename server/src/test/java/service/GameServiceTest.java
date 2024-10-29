package service;

import dataaccess.GameDAO;
import model.GameData;
import exceptions.GameNotFoundException;
import exceptions.InvalidPlayerColorException;
import exceptions.PlayerSpotTakenException;
import exceptions.ServiceException;
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
  public void testCreateGameSuccess() throws ServiceException {
    // Since createGame now accepts only the gameName
    GameData game = gameService.createGame("Chess Match 1");
    assertNotNull(game);
    assertEquals("Chess Match 1", game.gameName());
    // Both whiteUsername and blackUsername should be null upon creation
    assertNull(game.whiteUsername());
    assertNull(game.blackUsername());
  }

  @Test
  public void testListGames() throws ServiceException {
    gameService.createGame("Chess Match 1");
    gameService.createGame("Chess Match 2");

    var games = gameService.listGames();
    assertEquals(2, games.size());
  }

  @Test
  public void testJoinGameSuccess() throws Exception {
    // Create a game
    GameData game = gameService.createGame("Chess Match 1");
    int gameID = game.gameID();

    // Join the game as white
    gameService.joinGame(gameID, "WHITE", "john");

    // Retrieve the game and check that the whiteUsername is set
    GameData updatedGame = gameDAO.getGame(gameID);
    assertEquals("john", updatedGame.whiteUsername());
    assertNull(updatedGame.blackUsername());

    // Join the game as black
    gameService.joinGame(gameID, "BLACK", "jane");

    // Retrieve the game and check that both usernames are set
    updatedGame = gameDAO.getGame(gameID);
    assertEquals("john", updatedGame.whiteUsername());
    assertEquals("jane", updatedGame.blackUsername());
  }

  @Test
  public void testJoinGameSpotTaken() throws Exception {
    // Create a game
    GameData game = gameService.createGame("Chess Match 1");
    int gameID = game.gameID();

    // First player joins as white
    gameService.joinGame(gameID, "WHITE", "john");

    // Second player tries to join as white
    assertThrows(PlayerSpotTakenException.class, () -> {
      try {
        gameService.joinGame(gameID, "WHITE", "jane");
      } catch (ServiceException e) {
        fail("ServiceException occurred: " + e.getMessage());
      }
    });
  }

  @Test
  public void testJoinGameInvalidColor() throws Exception {
    // Create a game
    GameData game = gameService.createGame("Chess Match 1");
    int gameID = game.gameID();

    // Attempt to join with an invalid color
    assertThrows(InvalidPlayerColorException.class, () -> {
      try {
        gameService.joinGame(gameID, "GREEN", "john");
      } catch (ServiceException e) {
        fail("ServiceException occurred: " + e.getMessage());
      }
    });
  }

  @Test
  public void testJoinGameNotFound() throws ServiceException {
    // Attempt to join a non-existent game
    assertThrows(GameNotFoundException.class, () -> {
      try {
        gameService.joinGame(999, "WHITE", "john");
      } catch (ServiceException e) {
        fail("ServiceException occurred: " + e.getMessage());
      }
    });
  }
}
