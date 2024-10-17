package passoff.model;

public class TestListEntry {
  private Integer gameID;
  private String gameName;
  private String whiteUsername;
  private String blackUsername;

  public TestListEntry(Integer gameID, String gameName, String whiteUsername, String blackUsername) {
    this.gameID = gameID;
    this.gameName = gameName;
    this.whiteUsername = whiteUsername;
    this.blackUsername = blackUsername;
  }

  // Getters
  public Integer getGameID() {
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

  // Override equals and hashCode for use in assertions
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof TestListEntry)) return false;
    TestListEntry other = (TestListEntry) obj;
    return gameID.equals(other.gameID) &&
            gameName.equals(other.gameName) &&
            ((whiteUsername == null && other.whiteUsername == null) || (whiteUsername != null && whiteUsername.equals(other.whiteUsername))) &&
            ((blackUsername == null && other.blackUsername == null) || (blackUsername != null && blackUsername.equals(other.blackUsername)));
  }

  @Override
  public int hashCode() {
    int result = gameID.hashCode();
    result = 31 * result + gameName.hashCode();
    result = 31 * result + (whiteUsername != null ? whiteUsername.hashCode() : 0);
    result = 31 * result + (blackUsername != null ? blackUsername.hashCode() : 0);
    return result;
  }
}
