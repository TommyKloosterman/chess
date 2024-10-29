package dataaccess;

import model.GameData;
import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class GameDAO {

  private final Gson gson = new Gson();

  /**
   * Inserts a new game into the database.
   *
   * @param game The GameData object containing game information.
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public void insertGame(GameData game) throws DataAccessException {
    String sql = "INSERT INTO Games (game_name, state, white_player_id, black_player_id) VALUES (?, ?, ?, ?)";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setString(1, game.gameName());
      String stateJson = gson.toJson(game.game());
      stmt.setString(2, stateJson);

      // Get player IDs from usernames
      Integer whitePlayerId = getUserIdByUsername(game.whiteUsername());
      Integer blackPlayerId = getUserIdByUsername(game.blackUsername());

      if (whitePlayerId != null) {
        stmt.setInt(3, whitePlayerId);
      } else {
        stmt.setNull(3, Types.INTEGER);
      }

      if (blackPlayerId != null) {
        stmt.setInt(4, blackPlayerId);
      } else {
        stmt.setNull(4, Types.INTEGER);
      }

      stmt.executeUpdate();

      // Retrieve the generated game ID and set it in the GameData object
      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          int gameId = generatedKeys.getInt(1);
          game.setGameID(gameId);
        }
      }

    } catch (SQLException e) {
      throw new DataAccessException("Error inserting game: " + e.getMessage());
    }
  }

  /**
   * Updates an existing game.
   *
   * @param game The GameData object containing updated game information.
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public void updateGame(GameData game) throws DataAccessException {
    String sql = "UPDATE Games SET game_name = ?, state = ?, white_player_id = ?, black_player_id = ? WHERE game_id = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, game.gameName());
      String stateJson = gson.toJson(game.game());
      stmt.setString(2, stateJson);

      // Get player IDs from usernames
      Integer whitePlayerId = getUserIdByUsername(game.whiteUsername());
      Integer blackPlayerId = getUserIdByUsername(game.blackUsername());

      if (whitePlayerId != null) {
        stmt.setInt(3, whitePlayerId);
      } else {
        stmt.setNull(3, Types.INTEGER);
      }

      if (blackPlayerId != null) {
        stmt.setInt(4, blackPlayerId);
      } else {
        stmt.setNull(4, Types.INTEGER);
      }

      stmt.setInt(5, game.gameID());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new DataAccessException("Game not found with ID: " + game.gameID());
      }

    } catch (SQLException e) {
      throw new DataAccessException("Error updating game: " + e.getMessage());
    }
  }

  /**
   * Retrieves a game by its game ID.
   *
   * @param gameID The ID of the game to retrieve.
   * @return The GameData object if found, or null if not found.
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public GameData getGame(int gameID) throws DataAccessException {
    String sql = "SELECT * FROM Games WHERE game_id = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, gameID);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        String gameName = rs.getString("game_name");
        String stateJson = rs.getString("state");

        // Deserialize the game state
        ChessGame chessGame = gson.fromJson(stateJson, ChessGame.class);

        Integer whitePlayerId = rs.getObject("white_player_id", Integer.class);
        Integer blackPlayerId = rs.getObject("black_player_id", Integer.class);

        String whiteUsername = getUsernameByUserId(whitePlayerId);
        String blackUsername = getUsernameByUserId(blackPlayerId);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
      } else {
        return null; // Game not found
      }

    } catch (SQLException e) {
      throw new DataAccessException("Error retrieving game: " + e.getMessage());
    }
  }

  /**
   * Retrieves all games.
   *
   * @return A map of game IDs to GameData objects.
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public Map<Integer, GameData> listGames() throws DataAccessException {
    Map<Integer, GameData> games = new HashMap<>();
    String sql = "SELECT * FROM Games";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        int gameID = rs.getInt("game_id");
        String gameName = rs.getString("game_name");
        String stateJson = rs.getString("state");

        // Deserialize the game state
        ChessGame chessGame = gson.fromJson(stateJson, ChessGame.class);

        Integer whitePlayerId = rs.getObject("white_player_id", Integer.class);
        Integer blackPlayerId = rs.getObject("black_player_id", Integer.class);

        String whiteUsername = getUsernameByUserId(whitePlayerId);
        String blackUsername = getUsernameByUserId(blackPlayerId);

        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        games.put(gameID, gameData);
      }

    } catch (SQLException e) {
      throw new DataAccessException("Error listing games: " + e.getMessage());
    }
    return games;
  }

  /**
   * Clears all games from the database (used during testing).
   *
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public void clear() throws DataAccessException {
    String sql = "DELETE FROM Games";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.executeUpdate();

    } catch (SQLException e) {
      throw new DataAccessException("Error clearing games: " + e.getMessage());
    }
  }

  // Helper method to get user ID by username
  private Integer getUserIdByUsername(String username) throws DataAccessException {
    if (username == null) {
      return null;
    }
    String sql = "SELECT user_id FROM Users WHERE username = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return rs.getInt("user_id");
      } else {
        throw new DataAccessException("User not found: " + username);
      }

    } catch (SQLException e) {
      throw new DataAccessException("Error retrieving user ID: " + e.getMessage());
    }
  }

  // Helper method to get username by user ID
  private String getUsernameByUserId(Integer userId) throws DataAccessException {
    if (userId == null) {
      return null;
    }
    String sql = "SELECT username FROM Users WHERE user_id = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, userId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return rs.getString("username");
      } else {
        throw new DataAccessException("User ID not found: " + userId);
      }

    } catch (SQLException e) {
      throw new DataAccessException("Error retrieving username: " + e.getMessage());
    }
  }
}
