package exceptions;

public class AuthTokenNotFoundException extends Exception {
  public AuthTokenNotFoundException(String message) {
    super(message);
  }
}
