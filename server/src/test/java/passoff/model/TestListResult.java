package passoff.model;

public class TestListResult extends TestResult {
  private TestListEntry[] games;

  public TestListResult() {
    // Default constructor
  }

  // Getter and Setter
  public TestListEntry[] getGames() {
    return games;
  }

  public void setGames(TestListEntry[] games) {
    this.games = games;
  }
}
