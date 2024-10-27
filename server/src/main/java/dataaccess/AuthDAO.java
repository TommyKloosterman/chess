package dataaccess;

import model.AuthData;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {

  // Inserts an auth token into the database.
  public void insertAuth(AuthData authData) throws DataAccessException {
    String sql = "INSERT INTO auth_tokens (token, username) VALUES (?, ?)";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, authData.authToken());
      stmt.setString(2, authData.username());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Error inserting auth token: " + e.getMessage(), e);
    }
  }

  // Retrieves an auth token from the database.
  public AuthData getAuth(String authToken) throws DataAccessException {
    String sql = "SELECT username FROM auth_tokens WHERE token = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, authToken);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String username = rs.getString("username");
        return new AuthData(authToken, username);
      } else {
        return null; // Token not found
      }
    } catch (SQLException e) {
      throw new DataAccessException("Error retrieving auth token: " + e.getMessage(), e);
    }
  }

  // Deletes an auth token from the database.
  public void deleteAuth(String authToken) throws DataAccessException {
    String sql = "DELETE FROM auth_tokens WHERE token = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, authToken);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Error deleting auth token: " + e.getMessage(), e);
    }
  }

  // Clears all auth tokens from the database.
  public void clear() throws DataAccessException {
    String sql = "DELETE FROM auth_tokens";
    try (Connection conn = DatabaseManager.getConnection();
         Statement stmt = conn.createStatement()) {
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      throw new DataAccessException("Error clearing auth tokens: " + e.getMessage(), e);
    }
  }

  // Optional: Returns all auth tokens (if needed).
  public Map<String, AuthData> listAuthTokens() throws DataAccessException {
    String sql = "SELECT token, username FROM auth_tokens";
    Map<String, AuthData> authTokens = new HashMap<>();
    try (Connection conn = DatabaseManager.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        String token = rs.getString("token");
        String username = rs.getString("username");
        authTokens.put(token, new AuthData(token, username));
      }
      return authTokens;
    } catch (SQLException e) {
      throw new DataAccessException("Error listing auth tokens: " + e.getMessage(), e);
    }
  }
}
