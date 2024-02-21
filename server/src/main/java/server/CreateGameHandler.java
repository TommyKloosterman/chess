package server;

import service.GameService;
import spark.Request;
import spark.Response;

import static spark.Spark.post;

public class CreateGameHandler {

  private GameService gameService;

  public CreateGameHandler(GameService gameService) {
    this.gameService = gameService;
  }

  public void registerRoutes() {
    post("/game", this::createGame);
  }

  private Object createGame(Request req, Response res) {
    // Parse the request body to get game data
    // Call gameService.createGame() with the parsed data
    // Format and return the response
  }
}