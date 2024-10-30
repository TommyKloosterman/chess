package dataaccess;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information from the db.properties file.
     */
    static {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Load properties from db.properties
            try (InputStream propStream = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {

                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                String host = props.getProperty("db.host");
                int port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC", host, port, DATABASE_NAME);

                System.out.println("Database Name: " + DATABASE_NAME);
                System.out.println("User: " + USER);
                System.out.println("Connection URL: " + CONNECTION_URL);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Include it in your library path.", e);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to process db.properties. " + ex.getMessage(), ex);
        }
    }

    /**
     * Public no-argument constructor, required by reflection in tests.
     */
    public DatabaseManager() {
        // No additional initialization needed here
    }

    /**
     * Initializes the database by creating it and setting up tables.
     */
    public static void initializeDatabase() throws DataAccessException {
        createDatabase();
        initializeTables();
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        String createDbStatement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
        String connectionUrlWithoutDb = CONNECTION_URL.substring(0, CONNECTION_URL.lastIndexOf("/"));

        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection(connectionUrlWithoutDb, USER, PASSWORD);
            preparedStatement = conn.prepareStatement(createDbStatement);
            preparedStatement.executeUpdate();
            System.out.println("Database checked/created successfully.");
        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage(), e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close PreparedStatement: " + e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close Connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Initializes the database tables if they do not exist.
     */
    public static void initializeTables() throws DataAccessException {
        String createUserTable = "CREATE TABLE IF NOT EXISTS UserData ("
                + "username VARCHAR(50) PRIMARY KEY,"
                + "hashedPassword VARCHAR(60) NOT NULL,"
                + "email VARCHAR(100) NOT NULL"
                + ");";

        String createAuthTable = "CREATE TABLE IF NOT EXISTS AuthData ("
                + "authToken VARCHAR(36) PRIMARY KEY,"
                + "username VARCHAR(50) NOT NULL,"
                + "FOREIGN KEY (username) REFERENCES UserData(username)"
                + ");";

        String createGameTable = "CREATE TABLE IF NOT EXISTS GameData ("
                + "gameID INT AUTO_INCREMENT PRIMARY KEY,"
                + "whiteUsername VARCHAR(50),"
                + "blackUsername VARCHAR(50),"
                + "gameName VARCHAR(50),"
                + "gameState JSON,"
                + "FOREIGN KEY (whiteUsername) REFERENCES UserData(username),"
                + "FOREIGN KEY (blackUsername) REFERENCES UserData(username)"
                + ");";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(createUserTable);
            stmt.executeUpdate(createAuthTable);
            stmt.executeUpdate(createGameTable);
            System.out.println("Tables initialized successfully.");
        } catch (SQLException e) {
            throw new DataAccessException("Error initializing tables: " + e.getMessage(), e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close Statement: " + e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close Connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Static method to get a database connection.
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to connect to the database: " + e.getMessage(), e);
        }
    }

    /**
     * Instance method to get a database connection, required for compatibility with the test file.
     */
    public Connection getConnectionInstance() throws DataAccessException {
        return DatabaseManager.getConnection(); // Calls the static method
    }

    /**
     * Test method to confirm the database connection is working.
     */
    public static void testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            System.out.println("Connection to the database was successful!");
        } catch (DataAccessException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close Connection: " + e.getMessage());
                }
            }
        }
    }
}
