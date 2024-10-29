package service;

import java.util.Map;
import dataaccess.GameDAO;
import model.GameData;
import exceptions.GameNotFoundException;
import exceptions.InvalidPlayerColorException;
import dataaccess.DataAccessException;
import exceptions.PlayerSpotTakenException;
import exceptions.ServiceException;

public class GameService {
  private final GameDAO gameDAO;
  private int nextGameID = 1;

  // Constructor that takes the GameDAO as a dependency.
  public GameService(GameDAO gameDAO) {
    this.gameDAO = gameDAO;
  }

  // Creates a new game.
  public GameData createGame(String gameName) throws ServiceException {
    int gameID = nextGameID++;
    GameData game = new GameData(gameID, null, null, gameName, null);
    try {
      gameDAO.insertGame(game);
    } catch (DataAccessException e) {
      throw new ServiceException("Error creating game", e);
    }
    return game;
  }

  // Retrieves a list of all games.
  public Map<Integer, GameData> listGames() throws ServiceException {
    try {
      return gameDAO.listGames();
    } catch (DataAccessException e) {
      throw new ServiceException("Error listing games", e);
    }
  }

  // Allows a player to join a game as either the white or black player.
  public void joinGame(int gameID, String playerColor, String username)
          throws GameNotFoundException, InvalidPlayerColorException, PlayerSpotTakenException, ServiceException {
    try {
      // Retrieve the game by its gameID.
      GameData game = gameDAO.getGame(gameID);
      if (game == null) {
        throw new GameNotFoundException("Game with ID " + gameID + " not found.");
      }

      // Check if the requested color is available and assign the player.
      if (playerColor.equalsIgnoreCase("WHITE")) {
        if (game.whiteUsername() == null) {
          // Assign the user as the white player.
          game.setWhiteUsername(username);
          gameDAO.updateGame(game); // Update the game in the DAO
        } else {
          throw new PlayerSpotTakenException("White player spot already taken.");
        }
      } else if (playerColor.equalsIgnoreCase("BLACK")) {
        if (game.blackUsername() == null) {
          // Assign the user as the black player.
          game.setBlackUsername(username);
          gameDAO.updateGame(game); // Update the game in the DAO
        } else {
          throw new PlayerSpotTakenException("Black player spot already taken.");
        }
      } else {
        throw new InvalidPlayerColorException("Invalid player color: " + playerColor);
      }
    } catch (DataAccessException e) {
      throw new ServiceException("Error joining game", e);
    }
  }

  // Clears all game data
  public void clear() {
    try {
      gameDAO.clear();
      nextGameID = 1; // Reset nextGameID to 1
    } catch (DataAccessException e) {
      // Handle exception, perhaps log it
      System.err.println("Error clearing game data: " + e.getMessage());
    }
  }
}
