package service;

import dataaccess.AuthDAO;
import exceptions.InvalidAuthTokenException;
import dataaccess.DataAccessException;
import exceptions.ServiceException;
import model.AuthData;

import java.util.UUID;

public class AuthService {
  private final AuthDAO authDAO;

  // Constructor that takes the AuthDAO as a dependency.
  public AuthService(AuthDAO authDAO) {
    this.authDAO = authDAO;
  }

  // Generates a new auth token for the specified username.
  public AuthData generateAuthToken(String username) throws ServiceException {
    // Generate a random token using UUID.
    String authToken = UUID.randomUUID().toString();
    AuthData authData = new AuthData(authToken, username);
    try {
      authDAO.insertAuth(authData);
      return authData;
    } catch (DataAccessException e) {
      throw new ServiceException("Error generating auth token", e);
    }
  }

  // Invalidates an auth token (logout operation).
  public void invalidateAuthToken(String authToken) throws ServiceException, InvalidAuthTokenException {
    try {
      // Check if the auth token exists.
      AuthData authData = authDAO.getAuth(authToken);
      if (authData == null) {
        throw new InvalidAuthTokenException("Invalid auth token.");
      }
      authDAO.deleteAuth(authToken);
    } catch (DataAccessException e) {
      throw new ServiceException("Error invalidating auth token", e);
    }
  }

  // Verifies if the auth token is valid.
  public boolean isValidAuthToken(String authToken) throws ServiceException {
    try {
      // Return true if the auth token exists, false otherwise.
      return authDAO.getAuth(authToken) != null;
    } catch (DataAccessException e) {
      throw new ServiceException("Error verifying auth token", e);
    }
  }

  // Retrieves the AuthData for a given auth token.
  public AuthData getAuth(String authToken) throws ServiceException, InvalidAuthTokenException {
    try {
      AuthData authData = authDAO.getAuth(authToken);
      if (authData == null) {
        throw new InvalidAuthTokenException("Auth token not found.");
      }
      return authData;
    } catch (DataAccessException e) {
      throw new ServiceException("Error retrieving auth token", e);
    }
  }

  // Clears all authentication data.
  public void clear() throws ServiceException {
    try {
      authDAO.clear();
    } catch (DataAccessException e) {
      throw new ServiceException("Error clearing auth data", e);
    }
  }
}
