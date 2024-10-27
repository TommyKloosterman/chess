package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import exceptions.UserAlreadyExistsException;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidRequestException;
import exceptions.InvalidAuthTokenException;
import exceptions.ServiceException;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
  private final UserDAO userDAO;
  private final AuthService authService;

  // Constructor that takes the UserDAO and AuthService as dependencies.
  public UserService(UserDAO userDAO, AuthService authService) {
    this.userDAO = userDAO;
    this.authService = authService;
  }

  // Registers a new user.
  public AuthData register(UserData user) throws ServiceException, UserAlreadyExistsException, InvalidRequestException {
    if (user == null || user.username() == null || user.password() == null || user.email() == null) {
      throw new InvalidRequestException("Missing required user fields.");
    }
    try {
      if (userDAO.getUser(user.username()) != null) {
        throw new UserAlreadyExistsException("User already exists with username: " + user.username());
      }
      userDAO.insertUser(user);
      return authService.generateAuthToken(user.username());
    } catch (DataAccessException e) {
      throw new ServiceException("Error registering user", e);
    }
  }

  // Logs in an existing user.
  public AuthData login(UserData user) throws ServiceException, InvalidCredentialsException, InvalidRequestException {
    if (user == null || user.username() == null || user.password() == null) {
      throw new InvalidRequestException("Missing username or password.");
    }
    try {
      UserData storedUser = userDAO.getUser(user.username());
      if (storedUser == null || !BCrypt.checkpw(user.password(), storedUser.password())) {
        throw new InvalidCredentialsException("Invalid credentials.");
      }
      return authService.generateAuthToken(storedUser.username());
    } catch (DataAccessException e) {
      throw new ServiceException("Error logging in user", e);
    }
  }

  // Logs out the user by removing their auth token.
  public void logout(String authToken) throws ServiceException, InvalidAuthTokenException {
    try {
      authService.invalidateAuthToken(authToken);
    } catch (DataAccessException e) {
      throw new ServiceException("Error logging out user", e);
    }
  }

  // Clears all user data.
  public void clear() {
    try {
      userDAO.clear();
      authService.clear();
    } catch (DataAccessException e) {
      // Handle exception, perhaps log it
      System.err.println("Error clearing user data: " + e.getMessage());
    }
  }
}
