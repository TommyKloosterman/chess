package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  private UserDAO userDAO;
  private AuthDAO authDAO;
  private UserService userService;
  private AuthService authService;

  @BeforeEach
  public void setUp() {
    userDAO = new UserDAO();
    authDAO = new AuthDAO();
    authService = new AuthService(authDAO);
    userService = new UserService(userDAO, authService);
  }

  @Test
  public void testRegisterSuccess() throws DataAccessException {
    UserData user = new UserData("john", "password123", "john@example.com");
    AuthData authData = userService.register(user);

    assertNotNull(authData.authToken());
    assertEquals("john", authData.username());
  }

  @Test
  public void testRegisterUserAlreadyExists() {
    UserData user = new UserData("john", "password123", "john@example.com");

    assertThrows(DataAccessException.class, () -> {
      userService.register(user);
      userService.register(user);  // Should throw exception on second registration
    });
  }

  @Test
  public void testLoginSuccess() throws DataAccessException {
    UserData user = new UserData("john", "password123", "john@example.com");
    userService.register(user);  // Register the user first

    AuthData authData = userService.login(new UserData("john", "password123", null));
    assertNotNull(authData.authToken());
    assertEquals("john", authData.username());
  }

  @Test
  public void testLoginInvalidPassword() throws DataAccessException {
    UserData user = new UserData("john", "password123", "john@example.com");
    userService.register(user);  // Register the user first

    assertThrows(DataAccessException.class, () -> {
      userService.login(new UserData("john", "wrongpassword", null));
    });
  }

  @Test
  public void testLogoutSuccess() throws DataAccessException {
    UserData user = new UserData("john", "password123", "john@example.com");
    AuthData authData = userService.register(user);

    userService.logout(authData.authToken());
    assertFalse(authService.isValidAuthToken(authData.authToken()));
  }
}
