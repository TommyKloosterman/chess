package server;

import service.ClearService; // Assuming you have a service class for clearing operations
import spark.Request;
import spark.Response;

import static spark.Spark.delete;

public class ClearHandler {

  private ClearService clearService;

  public ClearHandler(ClearService clearService) {
    this.clearService = clearService;
  }

  public void registerRoutes() {
    delete("/db", this::clearDatabase);
  }

  private Object clearDatabase(Request req, Response res) {
    // Call the clearService method to clear the database
    // Format and return the response
  }
}
