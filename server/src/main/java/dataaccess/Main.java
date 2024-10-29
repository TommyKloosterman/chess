package dataaccess;

public class Main {
  public static void main(String[] args) {
    try {
      DatabaseManager.createDatabase();
      System.out.println("Database initialized successfully.");
    } catch (DataAccessException e) {
      System.out.println("Failed to initialize the database.");
      e.printStackTrace();
      return;
    }

    try {
      DatabaseManager.initializeTables();
      System.out.println("Tables initialized successfully.");
    } catch (DataAccessException e) {
      System.out.println("Failed to initialize tables.");
      e.printStackTrace();
      return;
    }

    DatabaseManager.testConnection();
  }
}
