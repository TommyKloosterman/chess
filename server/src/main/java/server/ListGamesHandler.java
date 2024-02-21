package server;

import service.GameService;
import spark.Request;
import spark.Response;

import static spark.Spark.get;

public class ListGamesHandler {

  private GameService gameService;

  public ListGamesHandler(GameService gameService) {
    this.gameService = gameService;
  }

  public void registerRoutes() {
    get("/game", this::listGames);
  }

  private Object listGames(Request req, Response res) {
    // Extract the authToken from the request header
    // Call gameService.listGames() with the authToken
    // Format and return the response
  }
}