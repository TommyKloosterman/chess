package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

  /**
   * Inserts a new user into the database.
   *
   * @param user The UserData object containing user information.
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public void insertUser(UserData user) throws DataAccessException {
    String sql = "INSERT INTO Users (username, password_hash, email) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      // Hash the password before storing
      String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

      stmt.setString(1, user.username());
      stmt.setString(2, hashedPassword);
      stmt.setString(3, user.email());
      stmt.executeUpdate();

    } catch (SQLException e) {
      if (e.getMessage().contains("Duplicate entry")) {
        throw new DataAccessException("User already exists: " + user.username());
      } else {
        throw new DataAccessException("Error inserting user: " + e.getMessage());
      }
    }
  }

  /**
   * Retrieves a user by their username.
   *
   * @param username The username to search for.
   * @return The UserData object if found, or null if not found.
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public UserData getUser(String username) throws DataAccessException {
    String sql = "SELECT * FROM Users WHERE username = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        String retrievedUsername = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String email = rs.getString("email");

        // Create a UserData object with the retrieved information
        return new UserData(retrievedUsername, passwordHash, email);
      } else {
        return null; // User not found
      }

    } catch (SQLException e) {
      throw new DataAccessException("Error retrieving user: " + e.getMessage());
    }
  }

  /**
   * Verifies a user's password.
   *
   * @param username        The username of the user.
   * @param providedPassword The password provided for verification.
   * @return True if the password matches, false otherwise.
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public boolean verifyPassword(String username, String providedPassword) throws DataAccessException {
    UserData user = getUser(username);
    if (user == null) {
      return false; // User does not exist
    }
    // Password stored in UserData is the hashed password
    return BCrypt.checkpw(providedPassword, user.password());
  }

  /**
   * Clears all users from the database (used during testing).
   *
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public void clear() throws DataAccessException {
    String sql = "DELETE FROM Users";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.executeUpdate();

    } catch (SQLException e) {
      throw new DataAccessException("Error clearing users: " + e.getMessage());
    }
  }

  /**
   * Retrieves all users from the database.
   *
   * @return A list of UserData objects.
   * @throws DataAccessException If an error occurs during the database operation.
   */
  public List<UserData> getAllUsers() throws DataAccessException {
    List<UserData> users = new ArrayList<>();
    String sql = "SELECT * FROM Users";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String email = rs.getString("email");
        users.add(new UserData(username, passwordHash, email));
      }

    } catch (SQLException e) {
      throw new DataAccessException("Error retrieving users: " + e.getMessage());
    }
    return users;
  }
}
