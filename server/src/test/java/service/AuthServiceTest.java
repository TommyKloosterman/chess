package service;

import dataaccess.AuthDAO;
import model.AuthData;
import exceptions.InvalidAuthTokenException;
import exceptions.ServiceException;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

  private AuthService authService;
  private AuthDAO authDAO;

  @BeforeEach
  void setUp() {
    authDAO = new AuthDAO();  // Use real instance, no mocking
    authService = new AuthService(authDAO);
  }

  @Test
  void testGenerateAuthToken() throws ServiceException, DataAccessException {
    // Generate auth token for a user
    AuthData authData = authService.generateAuthToken("username");
    assertNotNull(authData);
    assertEquals("username", authData.username());

    // Verify that the auth token was stored in the DAO
    AuthData retrievedAuthData = authDAO.getAuth(authData.authToken());
    assertEquals(authData.authToken(), retrievedAuthData.authToken());
  }

  @Test
  void testInvalidateAuthToken() throws ServiceException, InvalidAuthTokenException {
    // Generate and then invalidate an auth token
    AuthData authData = authService.generateAuthToken("username");

    // Ensure the token is valid before invalidation
    assertTrue(authService.isValidAuthToken(authData.authToken()));

    // Invalidate the token
    authService.invalidateAuthToken(authData.authToken());

    // Verify that the token is no longer valid
    assertFalse(authService.isValidAuthToken(authData.authToken()));
  }

  @Test
  void testIsValidAuthToken() throws ServiceException {
    // Generate auth token
    AuthData authData = authService.generateAuthToken("username");

    // Test valid token
    assertTrue(authService.isValidAuthToken(authData.authToken()));

    // Test invalid token
    assertFalse(authService.isValidAuthToken("invalidToken"));
  }

  @Test
  void testGetAuth() throws ServiceException, InvalidAuthTokenException {
    // Generate auth token and retrieve it
    AuthData authData = authService.generateAuthToken("username");

    // Test retrieving the auth data using a valid token
    AuthData retrievedAuthData = authService.getAuth(authData.authToken());
    assertEquals("username", retrievedAuthData.username());
  }

  @Test
  void testInvalidAuthToken() {
    // Test that trying to invalidate an invalid token throws InvalidAuthTokenException
    try {
      authService.invalidateAuthToken("invalidToken");
      fail("Expected InvalidAuthTokenException was not thrown");
    } catch (InvalidAuthTokenException | ServiceException e) {
      assertTrue(e instanceof InvalidAuthTokenException, "Expected InvalidAuthTokenException but got: " + e.getClass().getSimpleName());
    }
  }

  @Test
  void testClear() throws ServiceException {
    // Generate an auth token, then clear the DAO
    AuthData authData = authService.generateAuthToken("username");
    authService.clear();

    // Try to retrieve the token after clearing, expecting an exception
    assertThrows(InvalidAuthTokenException.class, () -> authService.getAuth(authData.authToken()));
  }
}
