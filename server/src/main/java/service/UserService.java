package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class UserService {
  private final UserDAO userDAO;
  private final AuthService authService;

  // Constructor that takes the UserDAO and AuthService as dependencies.
  public UserService(UserDAO userDAO, AuthService authService) {
    this.userDAO = userDAO;
    this.authService = authService;
  }

  // Registers a new user.
  public AuthData register(UserData user) throws DataAccessException {
    // Insert user into the DAO.
    userDAO.insertUser(user);
    // Generate an auth token.
    return authService.generateAuthToken(user.username());
  }

  // Logs in an existing user.
  public AuthData login(UserData user) throws DataAccessException {
    // Fetch user data from DAO.
    UserData storedUser = userDAO.getUser(user.username());
    // Check if the password matches.
    if (!storedUser.password().equals(user.password())) {
      throw new DataAccessException("Invalid password.");
    }
    // Generate a new auth token.
    return authService.generateAuthToken(storedUser.username());
  }

  // Logs out the user by removing their auth token.
  public void logout(String authToken) throws DataAccessException {
    authService.invalidateAuthToken(authToken);
  }

  // Clears all user data.
  public void clear() {
    userDAO.clear();
  }
}
