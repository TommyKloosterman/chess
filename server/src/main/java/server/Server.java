package server;

import spark.Spark;
import service.UserService;
import service.GameService;
import dataAccess.IDataAccess;

public class Server {

  private UserService userService;
  private GameService gameService;

  public Server(IDataAccess dataAccess) {
    this.userService = new UserService(dataAccess);
    this.gameService = new GameService(dataAccess);
  }

  public int run(int desiredPort) {
    Spark.port(desiredPort);

    // Set the location of static files if you have a web interface
    Spark.staticFiles.location("web");

    // Registering handlers
    registerHandlers();

    Spark.awaitInitialization();
    return Spark.port();
  }

  private void registerHandlers() {
    // Instantiate and register each handler class
    new ClearHandler(new ClearService(/* pass necessary dependencies */)).registerRoutes();
    new RegisterHandler(userService).registerRoutes();
    new LoginHandler(userService).registerRoutes();
    new LogoutHandler(userService).registerRoutes();
    new ListGamesHandler(gameService).registerRoutes();
    new CreateGameHandler(gameService).registerRoutes();
    new JoinGameHandler(gameService).registerRoutes();
    // ... register other handlers as needed
  }

  public void stop() {
    Spark.stop();
  }
}