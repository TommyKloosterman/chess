package service;

import dataaccess.GameDAO;
import model.GameData;
import exceptions.GameNotFoundException;
import exceptions.InvalidPlayerColorException;
import exceptions.PlayerSpotTakenException;
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
  public void testCreateGameSuccess() {
    // Since createGame now accepts only the gameName
    GameData game = gameService.createGame("Chess Match 1");
    assertNotNull(game);
    assertEquals("Chess Match 1", game.gameName());
    // Both whiteUsername and blackUsername should be null upon creation
    assertNull(game.whiteUsername());
    assertNull(game.blackUsername());
  }

  @Test
  public void testListGames() {
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
      gameService.joinGame(gameID, "WHITE", "jane");
    });
  }

  @Test
  public void testJoinGameInvalidColor() throws Exception {
    // Create a game
    GameData game = gameService.createGame("Chess Match 1");
    int gameID = game.gameID();

    // Attempt to join with an invalid color
    assertThrows(InvalidPlayerColorException.class, () -> {
      gameService.joinGame(gameID, "GREEN", "john");
    });
  }

  @Test
  public void testJoinGameNotFound() {
    // Attempt to join a non-existent game
    assertThrows(GameNotFoundException.class, () -> {
      gameService.joinGame(999, "WHITE", "john");
    });
  }
}
