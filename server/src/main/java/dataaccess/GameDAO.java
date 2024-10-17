package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

public class GameDAO {
  // Stores games with their gameID as the key.
  private final Map<Integer, GameData> games = new HashMap<>();

  // Inserts a new game into the database.
  public void insertGame(GameData game) {
    games.put(game.gameID(), game);
  }

  // Updates an existing game.
  public void updateGame(GameData game) {
    games.put(game.gameID(), game);
  }

  // Retrieves a game by its gameID.
  public GameData getGame(int gameID) {
    return games.get(gameID); // Returns null if not found
  }

  // Retrieves all games.
  public Map<Integer, GameData> listGames() {
    return new HashMap<>(games);
  }

  // Clears all games from the database (used during testing).
  public void clear() {
    games.clear();
  }
}
