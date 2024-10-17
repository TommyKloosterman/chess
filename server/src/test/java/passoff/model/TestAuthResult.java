package passoff.model;

public class TestAuthResult extends TestResult {
  private String username;
  private String authToken;

  public TestAuthResult() {
    // Default constructor
  }

  // Getters and Setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }
}
