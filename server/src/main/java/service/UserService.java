package service;

import dataAccess.IDataAccess;
import model.UserData;
import model.AuthData;

public class UserService {

  private IDataAccess dataAccess;

  public UserService(IDataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  public AuthData register(UserData user) {
    // Implementation for registering a user
  }

  public AuthData login(UserData user) {
    // Implementation for logging in a user
  }

  public void logout(String authToken) {
    // Implementation for logging out a user
  }
}
