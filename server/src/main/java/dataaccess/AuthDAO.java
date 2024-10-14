package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
  // Stores authentication tokens with the authToken as the key.
  private final Map<String, AuthData> authTokens = new HashMap<>();

  // Inserts a new auth token into the database.
  public void insertAuth(AuthData auth) throws DataAccessException {
    authTokens.put(auth.authToken(), auth);
  }

  // Retrieves an auth token by its value.
  public AuthData getAuth(String authToken) throws DataAccessException {
    if (!authTokens.containsKey(authToken)) {
      throw new DataAccessException("Auth token not found: " + authToken);
    }
    return authTokens.get(authToken);
  }

  // Deletes an auth token by its value.
  public void deleteAuth(String authToken) throws DataAccessException {
    if (!authTokens.containsKey(authToken)) {
      throw new DataAccessException("Auth token not found: " + authToken);
    }
    authTokens.remove(authToken);
  }

  // Clears all auth tokens from the database (used during testing).
  public void clear() {
    authTokens.clear();
  }
}
