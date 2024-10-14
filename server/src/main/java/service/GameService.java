package service;

import java.util.Map;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;

public class GameService {
  private final GameDAO gameDAO;

  // Constructor that takes the GameDAO as a dependency.
  public GameService(GameDAO gameDAO) {
    this.gameDAO = gameDAO;
  }

  // Creates a new game.
  public GameData createGame(String gameName, String whiteUsername) throws DataAccessException {
    // Generate a unique gameID (this can be more sophisticated).
    int gameID = gameDAO.listGames().size() + 1;
    // Create a new game with the given name and white player.
    GameData game = new GameData(gameID, whiteUsername, null, gameName, null);
    gameDAO.insertGame(game);
    return game;
  }

  // Retrieves a list of all games.
  public Map<Integer, GameData> listGames() {
    return gameDAO.listGames();
  }

  // Allows a player to join a game as either the white or black player.
  public void joinGame(int gameID, String playerColor, String username) throws DataAccessException {
    // Retrieve the game by its gameID.
    GameData game = gameDAO.getGame(gameID);

    // Check if the requested color is available and assign the player.
    if (playerColor.equalsIgnoreCase("WHITE")) {
      if (game.whiteUsername() == null) {
        // Assign the user as the white player.
        game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        gameDAO.insertGame(game);  // Update the game in the DAO
      } else {
        throw new DataAccessException("White player spot already taken.");
      }
    } else if (playerColor.equalsIgnoreCase("BLACK")) {
      if (game.blackUsername() == null) {
        // Assign the user as the black player.
        game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        gameDAO.insertGame(game);  // Update the game in the DAO
      } else {
        throw new DataAccessException("Black player spot already taken.");
      }
    } else {
      throw new DataAccessException("Invalid player color.");
    }
  }

  // Clears all game data.
  public void clear() {
    gameDAO.clear();
  }
}
