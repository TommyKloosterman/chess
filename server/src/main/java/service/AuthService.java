package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.UUID;

public class AuthService {
  private final AuthDAO authDAO;

  // Constructor that takes the AuthDAO as a dependency.
  public AuthService(AuthDAO authDAO) {
    this.authDAO = authDAO;
  }

  // Generates a new auth token for the specified username.
  public AuthData generateAuthToken(String username) throws DataAccessException {
    // Generate a random token using UUID.
    String authToken = UUID.randomUUID().toString();
    AuthData authData = new AuthData(authToken, username);
    authDAO.insertAuth(authData);
    return authData;
  }

  // Invalidates an auth token (logout operation).
  public void invalidateAuthToken(String authToken) throws DataAccessException {
    authDAO.deleteAuth(authToken);
  }

  // Verifies if the auth token is valid.
  public boolean isValidAuthToken(String authToken) throws DataAccessException {
    try {
      authDAO.getAuth(authToken);
      return true;
    } catch (DataAccessException e) {
      return false;
    }
  }

  // Clears all authentication data.
  public void clear() {
    authDAO.clear();
  }
}
