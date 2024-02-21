package server;

import service.UserService;
import model.UserData;
import spark.Request;
import spark.Response;

import static spark.Spark.post;

public class LoginHandler {

  private UserService userService;

  public LoginHandler(UserService userService) {
    this.userService = userService;
  }

  public void registerRoutes() {
    post("/session", this::loginUser);
  }

  private Object loginUser(Request req, Response res) {
    // Parse the request body to get user data
    // Call userService.login() with the parsed data
    // Format and return the response
  }
}