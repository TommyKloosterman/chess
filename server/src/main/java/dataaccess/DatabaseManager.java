package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;
    private static final String BASE_CONNECTION_URL;

    /*
     * Load the database information from the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));

                // Base connection URL without database name
                BASE_CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);

                // Full connection URL with database name
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d/%s", host, port, DATABASE_NAME);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to process db.properties: " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (Connection conn = DriverManager.getConnection(BASE_CONNECTION_URL, USER, PASSWORD)) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage());
        }
    }

    /**
     * Initializes the database by creating tables if they don't exist.
     */
    public static void initializeDatabase() throws DataAccessException {
        createDatabase();  // Ensure the database exists

        try (Connection conn = getConnection()) {
            // Create Users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS Users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(60) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL
                );
            """;
            try (PreparedStatement stmt = conn.prepareStatement(createUsersTable)) {
                stmt.executeUpdate();
            }

            // Create Games table
            String createGamesTable = """
                CREATE TABLE IF NOT EXISTS Games (
                    game_id INT AUTO_INCREMENT PRIMARY KEY,
                    game_name VARCHAR(100) NOT NULL,
                    state JSON,  -- Store serialized game state as JSON
                    white_player_id INT,
                    black_player_id INT,
                    FOREIGN KEY (white_player_id) REFERENCES Users(user_id),
                    FOREIGN KEY (black_player_id) REFERENCES Users(user_id)
                );
            """;
            try (PreparedStatement stmt = conn.prepareStatement(createGamesTable)) {
                stmt.executeUpdate();
            }

            // Optionally, add more tables here (like Moves) as needed for your project
        } catch (SQLException e) {
            throw new DataAccessException("Error initializing database tables: " + e.getMessage());
        }
    }

    /**
     * Create a connection to the database using the properties in db.properties.
     * Connections should be short-lived; always close the connection when done.
     * Use a try-with-resource block to ensure automatic resource management.
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException("Error establishing database connection: " + e.getMessage());
        }
    }

    /**
     * Test connection to the database.
     * This is a temporary test method to verify the database setup.
     */
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
        } catch (DataAccessException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
