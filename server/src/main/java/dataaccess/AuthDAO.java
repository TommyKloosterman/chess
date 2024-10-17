package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
  private final Map<String, AuthData> authTokenMap = new HashMap<>();

  // Inserts an auth token into the map
  public void insertAuth(AuthData authData) {
    authTokenMap.put(authData.authToken(), authData);
  }

  // Retrieves an auth token
  public AuthData getAuth(String authToken) throws DataAccessException {
    if (authTokenMap.containsKey(authToken)) {
      return authTokenMap.get(authToken);
    } else {
      throw new DataAccessException("Auth token not found.");
    }
  }

  // Deletes an auth token
  public void deleteAuth(String authToken) throws DataAccessException {
    if (authTokenMap.containsKey(authToken)) {
      authTokenMap.remove(authToken);
    } else {
      throw new DataAccessException("Auth token not found.");
    }
  }

  // Clears all auth tokens
  public void clear() {
    authTokenMap.clear();
  }

  // NEW: Returns all auth tokens
  public Map<String, AuthData> listAuthTokens() {
    return new HashMap<>(authTokenMap);  // Return a copy of the auth tokens map
  }
}
