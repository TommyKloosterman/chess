package server;

import service.GameService;
import spark.Request;
import spark.Response;

import static spark.Spark.put;

public class JoinGameHandler {

  private GameService gameService;

  public JoinGameHandler(GameService gameService) {
    this.gameService = gameService;
  }

  public void registerRoutes() {
    put("/game", this::joinGame);
  }

  private Object joinGame(Request req, Response res) {
    // Parse the request body to get game and player details
    // Call gameService.joinGame() with the parsed data
    // Format and return the response
  }
}