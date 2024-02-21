package server;

import service.UserService;
import spark.Request;
import spark.Response;

import static spark.Spark.delete;

public class LogoutHandler {

  private UserService userService;

  public LogoutHandler(UserService userService) {
    this.userService = userService;
  }

  public void registerRoutes() {
    delete("/session", this::logoutUser);
  }

  private Object logoutUser(Request req, Response res) {
    // Extract the authToken from the request header
    // Call userService.logout() with the authToken
    // Format and return the response
  }
}