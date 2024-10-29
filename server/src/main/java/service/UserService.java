package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;  // Ensure this import is here
import exceptions.*;
import model.AuthData;
import model.UserData;
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
      // Hash the password before storing
      String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
      UserData userWithHashedPassword = new UserData(user.username(), hashedPassword, user.email());
      userDAO.insertUser(userWithHashedPassword);

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
    authService.invalidateAuthToken(authToken);
  }

  // Clears all user data.
  public void clear() throws ServiceException {
    try {
      // Clear auth tokens first to avoid foreign key constraints
      authService.clear();
      userDAO.clear();
    } catch (DataAccessException e) {
      throw new ServiceException("Error clearing user data", e);
    }
  }
}
