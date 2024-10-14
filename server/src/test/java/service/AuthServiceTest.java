package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

  private AuthDAO authDAO;
  private AuthService authService;

  @BeforeEach
  public void setUp() {
    authDAO = new AuthDAO();
    authService = new AuthService(authDAO);
  }

  @Test
  public void testGenerateAuthTokenSuccess() throws DataAccessException {
    AuthData authData = authService.generateAuthToken("john");
    assertNotNull(authData.authToken());
    assertEquals("john", authData.username());
  }

  @Test
  public void testIsValidAuthTokenSuccess() throws DataAccessException {
    AuthData authData = authService.generateAuthToken("john");
    assertTrue(authService.isValidAuthToken(authData.authToken()));
  }

  @Test
  public void testInvalidateAuthTokenSuccess() throws DataAccessException {
    AuthData authData = authService.generateAuthToken("john");
    authService.invalidateAuthToken(authData.authToken());
    assertFalse(authService.isValidAuthToken(authData.authToken()));
  }
}
