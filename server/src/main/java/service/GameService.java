package service;

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

  // Clears all game data.
  public void clear() {
    gameDAO.clear();
  }
}
