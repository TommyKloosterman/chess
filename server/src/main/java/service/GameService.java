package service;

import java.util.Map;
import dataaccess.GameDAO;
import model.GameData;
import exceptions.GameNotFoundException;
import exceptions.InvalidPlayerColorException;
import exceptions.PlayerSpotTakenException;

public class GameService {
  private final GameDAO gameDAO;
  private int nextGameID = 1;

  // Constructor that takes the GameDAO as a dependency.
  public GameService(GameDAO gameDAO) {
    this.gameDAO = gameDAO;
  }

  // Creates a new game.
  public GameData createGame(String gameName) {
    int gameID = nextGameID++;
    GameData game = new GameData(gameID, null, null, gameName, null);
    gameDAO.insertGame(game);
    return game;
  }

  // Retrieves a list of all games.
  public Map<Integer, GameData> listGames() {
    return gameDAO.listGames();
  }

  // Allows a player to join a game as either the white or black player.
  public void joinGame(int gameID, String playerColor, String username)
          throws GameNotFoundException, InvalidPlayerColorException, PlayerSpotTakenException {
    // Retrieve the game by its gameID.
    GameData game = gameDAO.getGame(gameID);
    if (game == null) {
      throw new GameNotFoundException("Game with ID " + gameID + " not found.");
    }

    // Check if the requested color is available and assign the player.
    if (playerColor.equalsIgnoreCase("WHITE")) {
      if (game.whiteUsername() == null) {
        // Assign the user as the white player.
        game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        gameDAO.updateGame(game); // Update the game in the DAO
      } else {
        throw new PlayerSpotTakenException("White player spot already taken.");
      }
    } else if (playerColor.equalsIgnoreCase("BLACK")) {
      if (game.blackUsername() == null) {
        // Assign the user as the black player.
        game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        gameDAO.updateGame(game); // Update the game in the DAO
      } else {
        throw new PlayerSpotTakenException("Black player spot already taken.");
      }
    } else {
      throw new InvalidPlayerColorException("Invalid player color: " + playerColor);
    }
  }

  // Clears all game data
  public void clear() {
    gameDAO.clear();
    nextGameID = 1; // Reset nextGameID to 1
  }
}
