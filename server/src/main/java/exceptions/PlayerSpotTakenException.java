package exceptions;

public class PlayerSpotTakenException extends Exception {
  public PlayerSpotTakenException(String message) {
    super(message);
  }
}
