package passoff.model;

public class TestUser {
  private String username;
  private String password;
  private String email;

  // Constructor with username and password
  public TestUser(String username, String password) {
    this.username = username;
    this.password = password;
    this.email = null; // Set email to null or a default value
  }

  // Existing constructor with username, password, and email
  public TestUser(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  // Getters
  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }
}
