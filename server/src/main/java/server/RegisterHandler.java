package server;

import service.UserService; // Your UserService class
import model.UserData;
import spark.Request;
import spark.Response;

import static spark.Spark.post;

public class RegisterHandler {

  private UserService userService;

  public RegisterHandler(UserService userService) {
    this.userService = userService;
  }

  public void registerRoutes() {
    post("/user", this::registerUser);
  }

  private Object registerUser(Request req, Response res) {
    // Parse the request body to get user data
    // Call userService.register() with the parsed data
    // Format and return the response
  }
}