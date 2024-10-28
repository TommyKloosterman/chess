package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
            String statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (Connection conn = DriverManager.getConnection(BASE_CONNECTION_URL, USER, PASSWORD);
                 var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage(), e);
        }
    }

    /**
     * Initializes the database by dropping and creating tables.
     */
    public static void initializeDatabase() {
        try {
            createDatabase();  // Ensure the database exists

            try (Connection conn = getConnection()) {
                // Drop tables if they exist
                String dropAuthTokensTable = "DROP TABLE IF EXISTS auth_tokens;";
                String dropGamesTable = "DROP TABLE IF EXISTS Games;";
                String dropUsersTable = "DROP TABLE IF EXISTS Users;";

                try (var stmt = conn.prepareStatement(dropAuthTokensTable)) {
                    stmt.executeUpdate();
                }
                try (var stmt = conn.prepareStatement(dropGamesTable)) {
                    stmt.executeUpdate();
                }
                try (var stmt = conn.prepareStatement(dropUsersTable)) {
                    stmt.executeUpdate();
                }

                // Create Users table
                String createUsersTable = """
                CREATE TABLE Users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(60) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL
                );
            """;
                try (var stmt = conn.prepareStatement(createUsersTable)) {
                    stmt.executeUpdate();
                }

                // Create Games table
                String createGamesTable = """
                CREATE TABLE Games (
                    game_id INT AUTO_INCREMENT PRIMARY KEY,
                    game_name VARCHAR(100) NOT NULL,
                    state JSON,
                    white_player_id INT,
                    black_player_id INT,
                    FOREIGN KEY (white_player_id) REFERENCES Users(user_id),
                    FOREIGN KEY (black_player_id) REFERENCES Users(user_id)
                );
            """;
                try (var stmt = conn.prepareStatement(createGamesTable)) {
                    stmt.executeUpdate();
                }

                // Create Auth Tokens table
                String createAuthTokensTable = """
                CREATE TABLE auth_tokens (
                    token VARCHAR(100) PRIMARY KEY,
                    username VARCHAR(50) NOT NULL,
                    FOREIGN KEY (username) REFERENCES Users(username)
                );
            """;
                try (var stmt = conn.prepareStatement(createAuthTokensTable)) {
                    stmt.executeUpdate();
                }

            }
        } catch (DataAccessException e) {
            System.err.println("Error accessing database: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Provides a connection to the database using the properties in db.properties.
     * Connections should be short-lived; always close the connection when done.
     * Use a try-with-resources block to ensure automatic resource management.
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("Error establishing database connection: " + e.getMessage(), e);
        }
    }
}
