package model;

public class Game {
  private int gameID;
  private String gameName;
  private String whiteUsername; // Initially null
  private String blackUsername; // Initially null

  // Constructor
  public Game(int gameID, String gameName) {
    this.gameID = gameID;
    this.gameName = gameName;
    this.whiteUsername = null;
    this.blackUsername = null;
  }

  // Getter and Setter methods
  public int getGameID() {
    return gameID;
  }

  public String getGameName() {
    return gameName;
  }

  public String getWhiteUsername() {
    return whiteUsername;
  }

  public String getBlackUsername() {
    return blackUsername;
  }

  public void setWhiteUsername(String whiteUsername) {
    this.whiteUsername = whiteUsername;
  }

  public void setBlackUsername(String blackUsername) {
    this.blackUsername = blackUsername;
  }

  // Method to join a team
  public boolean joinTeam(String teamColor, String username) {
    if (teamColor.equalsIgnoreCase("WHITE")) {
      if (whiteUsername == null) {
        whiteUsername = username;
        return true;
      } else {
        return false; // White team slot is already taken
      }
    } else if (teamColor.equalsIgnoreCase("BLACK")) {
      if (blackUsername == null) {
        blackUsername = username;
        return true;
      } else {
        return false; // Black team slot is already taken
      }
    } else {
      // Invalid team color
      return false;
    }
  }

  public boolean isPlayerInGame(String username) {
    return username.equals(whiteUsername) || username.equals(blackUsername);
  }
}
