package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import exceptions.UserAlreadyExistsException;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidRequestException;
import exceptions.InvalidAuthTokenException;

public class UserService {
  private final UserDAO userDAO;
  private final AuthService authService;

  // Constructor that takes the UserDAO and AuthService as dependencies.
  public UserService(UserDAO userDAO, AuthService authService) {
    this.userDAO = userDAO;
    this.authService = authService;
  }

  // Registers a new user.
  public AuthData register(UserData user) throws DataAccessException, UserAlreadyExistsException, InvalidRequestException {
    if (user == null || user.username() == null || user.password() == null || user.email() == null) {
      throw new InvalidRequestException("Missing required user fields.");
    }
    if (userDAO.getUser(user.username()) != null) {
      throw new UserAlreadyExistsException("User already exists with username: " + user.username());
    }
    userDAO.insertUser(user);
    return authService.generateAuthToken(user.username());
  }

  // Logs in an existing user.
  public AuthData login(UserData user) throws DataAccessException, InvalidCredentialsException, InvalidRequestException {
    if (user == null || user.username() == null || user.password() == null) {
      throw new InvalidRequestException("Missing username or password.");
    }
    UserData storedUser = userDAO.getUser(user.username());
    if (storedUser == null || !storedUser.password().equals(user.password())) {
      throw new InvalidCredentialsException("Invalid credentials.");
    }
    return authService.generateAuthToken(storedUser.username());
  }

  // Logs out the user by removing their auth token.
  public void logout(String authToken) throws DataAccessException, InvalidAuthTokenException {
    authService.invalidateAuthToken(authToken);
  }

  // Clears all user data.
  public void clear() {
    userDAO.clear();
    authService.clear();
  }
}
