package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidRequestException;
import exceptions.InvalidAuthTokenException;
import exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

  private UserDAO userDAO;
  private AuthService authService;
  private AuthDAO authDAO;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userDAO = new UserDAO();  // Create actual instances, no mocking
    authDAO = new AuthDAO();
    authService = new AuthService(authDAO);  // Pass actual DAO into service
    userService = new UserService(userDAO, authService);
  }

  @Test
  void testRegisterNewUser() throws DataAccessException, UserAlreadyExistsException, InvalidRequestException {
    UserData newUser = new UserData("username", "password", "email@example.com");

    // Test that the user can be registered
    AuthData authData = userService.register(newUser);
    assertNotNull(authData);
    assertEquals("username", authData.username());

    // Ensure user is registered
    UserData retrievedUser = userDAO.getUser("username");
    assertEquals(newUser.username(), retrievedUser.username());
  }

  @Test
  void testRegisterExistingUser() throws DataAccessException, InvalidRequestException, UserAlreadyExistsException {
    UserData newUser = new UserData("username", "password", "email@example.com");

    // Register the user the first time
    userService.register(newUser);

    // Try to register again and expect exception
    assertThrows(UserAlreadyExistsException.class, () -> userService.register(newUser));
  }

  @Test
  void testLoginSuccess() throws DataAccessException, UserAlreadyExistsException, InvalidCredentialsException, InvalidRequestException {
    UserData newUser = new UserData("username", "password", "email@example.com");
    userService.register(newUser);

    // Test successful login
    AuthData authData = userService.login(newUser);
    assertNotNull(authData);
    assertEquals("username", authData.username());
  }

  @Test
  void testLoginInvalidPassword() throws DataAccessException, UserAlreadyExistsException, InvalidRequestException {
    UserData newUser = new UserData("username", "password", "email@example.com");
    userService.register(newUser);

    // Test login with incorrect password
    UserData wrongPasswordUser = new UserData("username", "wrongPassword", "email@example.com");
    assertThrows(InvalidCredentialsException.class, () -> userService.login(wrongPasswordUser));
  }

  @Test
  void testLoginUserNotFound() throws DataAccessException, InvalidRequestException {
    // Test login for non-existent user
    UserData nonExistentUser = new UserData("nonexistent", "password", "email@example.com");
    assertThrows(InvalidCredentialsException.class, () -> userService.login(nonExistentUser));
  }

  @Test
  void testLogout() throws DataAccessException, UserAlreadyExistsException, InvalidRequestException {
    UserData newUser = new UserData("username", "password", "email@example.com");
    AuthData authData = userService.register(newUser);

    try {
      // Test logout
      userService.logout(authData.authToken());

      // Ensure auth token is invalid after logout
      assertFalse(authService.isValidAuthToken(authData.authToken()));
    } catch (InvalidAuthTokenException e) {
      fail("Unexpected InvalidAuthTokenException thrown: " + e.getMessage());
    }
  }
}
