package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
  // Stores users with their username as the key.
  private final Map<String, UserData> users = new HashMap<>();

  // Inserts a new user into the database.
  public void insertUser(UserData user) throws DataAccessException {
    if (users.containsKey(user.username())) {
      throw new DataAccessException("User already exists: " + user.username());
    }
    users.put(user.username(), user);
  }

  // Retrieves a user by their username.
  public UserData getUser(String username) {
    // Return the user if found, or null if not found.
    return users.get(username);
  }

  // Clears all users from the database (used during testing).
  public void clear() {
    users.clear();
  }
}
