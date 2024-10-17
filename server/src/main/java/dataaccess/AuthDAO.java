package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
  private final Map<String, AuthData> authTokenMap = new HashMap<>();

  // Inserts an auth token into the map.
  public void insertAuth(AuthData authData) {
    authTokenMap.put(authData.authToken(), authData);
  }

  // Retrieves an auth token.
  public AuthData getAuth(String authToken) {
    // Return the auth data if found, or null if not found.
    return authTokenMap.get(authToken);
  }

  // Deletes an auth token.
  public void deleteAuth(String authToken) {
    authTokenMap.remove(authToken);
  }

  // Clears all auth tokens.
  public void clear() {
    authTokenMap.clear();
  }

  // Returns all auth tokens (optional, as needed).
  public Map<String, AuthData> listAuthTokens() {
    return new HashMap<>(authTokenMap);  // Return a copy of the auth tokens map.
  }
}
