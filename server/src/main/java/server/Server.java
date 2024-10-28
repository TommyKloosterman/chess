package server;

import spark.Spark;
import service.UserService;
import service.GameService;
import service.AuthService;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import com.google.gson.Gson;
import model.UserData;
import model.AuthData;
import model.GameData;
import exceptions.UserAlreadyExistsException;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidRequestException;
import exceptions.InvalidAuthTokenException;
import exceptions.PlayerSpotTakenException;
import exceptions.InvalidPlayerColorException;
import exceptions.GameNotFoundException;
import java.util.Objects;

public class Server {
    private static final Gson gson = new Gson();
    private static boolean isInitialized = false;

    private UserService userService;
    private GameService gameService;
    private AuthService authService;

    public int run(int desiredPort) throws DataAccessException {
        if (!isInitialized) {
            isInitialized = true;

            // Set the port before mapping any routes
            Spark.port(desiredPort);
            Spark.staticFiles.location("web");

            // Initialize services, DAOs, etc.
            initializeServices();

            // Map your routes
            mapRoutes();

            // Start the server
            Spark.init();
            Spark.awaitInitialization();
        }

        return Spark.port();
    }

    private void initializeServices() {
        AuthDAO sharedAuthDAO = new AuthDAO();
        authService = new AuthService(sharedAuthDAO);
        userService = new UserService(new UserDAO(), authService);
        gameService = new GameService(new GameDAO());
    }

    private void mapRoutes() {
        // Clear Application Data
        Spark.delete("/db", (req, res) -> {
            try {
                // **Important:** Delete games first to avoid foreign key constraints
                gameService.clear();
                userService.clear();
                authService.clear();
                res.status(200);
                return gson.toJson(new EmptyResponse());
            } catch (Exception e) { // Catching generic Exception for broad error handling
                System.err.println("Error clearing database: " + e.getMessage());
                e.printStackTrace();
                res.status(500); // Internal Server Error
                return gson.toJson(new ErrorResponse("Error clearing database: " + e.getMessage()));
            }
        });

        // Register User
        Spark.post("/user", (req, res) -> {
            UserData user = gson.fromJson(req.body(), UserData.class);
            if (user == null || user.username() == null || user.password() == null || user.email() == null) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            try {
                AuthData authData = userService.register(user);
                res.status(200);
                return gson.toJson(authData);
            } catch (UserAlreadyExistsException e) {
                res.status(403); // Forbidden
                return gson.toJson(new ErrorResponse("Error: username already taken"));
            } catch (InvalidRequestException e) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
        });

        // Login User
        Spark.post("/session", (req, res) -> {
            UserData user = gson.fromJson(req.body(), UserData.class);
            if (user == null || user.username() == null || user.password() == null) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            try {
                AuthData authData = userService.login(user);
                res.status(200);
                return gson.toJson(authData);
            } catch (InvalidCredentialsException e) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: invalid credentials"));
            } catch (InvalidRequestException e) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
        });

        // Logout User
        Spark.delete("/session", (req, res) -> {
            String authToken = req.headers("Authorization");
            if (authToken == null) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            try {
                userService.logout(authToken);
                res.status(200);
                return gson.toJson(new EmptyResponse());
            } catch (InvalidAuthTokenException e) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });

        // List Games
        Spark.get("/game", (req, res) -> {
            String authToken = req.headers("Authorization");
            try {
                if (authToken == null) {
                    res.status(401); // Unauthorized
                    return gson.toJson(new ErrorResponse("Error: unauthorized"));
                }
                AuthData authData = authService.getAuth(authToken);

                var games = gameService.listGames();

                TestListEntry[] gameEntries = games.values().stream()
                        .map(game -> new TestListEntry(
                                game.gameID(),
                                game.gameName(),
                                game.whiteUsername(),
                                game.blackUsername()))
                        .toArray(TestListEntry[]::new);

                TestListResult listResult = new TestListResult();
                listResult.setGames(gameEntries);

                res.status(200);
                return gson.toJson(listResult);
            } catch (InvalidAuthTokenException e) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            } catch (Exception e) { // Catching generic Exception for broad error handling
                System.err.println("Error listing games: " + e.getMessage());
                e.printStackTrace();
                res.status(500); // Internal Server Error
                return gson.toJson(new ErrorResponse("Error listing games: " + e.getMessage()));
            }
        });

        // Create Game
        Spark.post("/game", (req, res) -> {
            String authToken = req.headers("Authorization");
            try {
                if (authToken == null) {
                    res.status(401); // Unauthorized
                    return gson.toJson(new ErrorResponse("Error: unauthorized"));
                }
                AuthData authData = authService.getAuth(authToken);

                var body = gson.fromJson(req.body(), GameRequest.class);
                if (body == null || body.gameName() == null) {
                    throw new InvalidRequestException("Game name is required.");
                }
                GameData game = gameService.createGame(body.gameName());
                TestCreateResult createResult = new TestCreateResult();
                createResult.setGameID(game.gameID());
                res.status(200);
                return gson.toJson(createResult);
            } catch (InvalidRequestException e) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            } catch (InvalidAuthTokenException e) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            } catch (Exception e) { // Catching generic Exception for broad error handling
                System.err.println("Error creating game: " + e.getMessage());
                e.printStackTrace();
                res.status(500); // Internal Server Error
                return gson.toJson(new ErrorResponse("Error creating game: " + e.getMessage()));
            }
        });

        // Join Game
        Spark.put("/game", (req, res) -> {
            String authToken = req.headers("Authorization");
            try {
                if (authToken == null) {
                    res.status(401); // Unauthorized
                    return gson.toJson(new ErrorResponse("Error: unauthorized"));
                }
                AuthData authData = authService.getAuth(authToken);

                var body = gson.fromJson(req.body(), JoinGameRequest.class);
                if (body == null || body.playerColor() == null || body.gameID() == 0) {
                    throw new InvalidRequestException("Missing player color or game ID.");
                }
                gameService.joinGame(body.gameID(), body.playerColor(), authData.username());
                res.status(200);
                return gson.toJson(new EmptyResponse());
            } catch (PlayerSpotTakenException e) {
                res.status(403); // Forbidden
                return gson.toJson(new ErrorResponse("Error: already taken"));
            } catch (GameNotFoundException e) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: invalid game ID"));
            } catch (InvalidPlayerColorException | InvalidRequestException e) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            } catch (InvalidAuthTokenException e) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            } catch (Exception e) { // Catching generic Exception for broad error handling
                System.err.println("Error joining game: " + e.getMessage());
                e.printStackTrace();
                res.status(500); // Internal Server Error
                return gson.toJson(new ErrorResponse("Error joining game: " + e.getMessage()));
            }
        });
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
        isInitialized = false; // Reset the initialization flag
    }
}

// Supporting Classes

class EmptyResponse {
    // Empty response class to use when no body is required in the response
}

class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

class GameRequest {
    private String gameName;

    public String gameName() {
        return gameName;
    }
}

class JoinGameRequest {
    private int gameID;
    private String playerColor;

    public int gameID() {
        return gameID;
    }

    public String playerColor() {
        return playerColor;
    }
}

class TestListEntry {
    private int gameID;
    private String gameName;
    private String whiteUsername;
    private String blackUsername;

    public TestListEntry(int gameID, String gameName, String whiteUsername, String blackUsername) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
    }

    public int getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TestListEntry)) return false;
        TestListEntry other = (TestListEntry) obj;
        return gameID == other.gameID &&
                Objects.equals(gameName, other.gameName) &&
                Objects.equals(whiteUsername, other.whiteUsername) &&
                Objects.equals(blackUsername, other.blackUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, gameName, whiteUsername, blackUsername);
    }

    @Override
    public String toString() {
        return "TestListEntry{" +
                "gameID=" + gameID +
                ", gameName='" + gameName + '\'' +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                '}';
    }
}

class TestCreateResult {
    private int gameID;

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}

class TestListResult {
    private TestListEntry[] games;

    public TestListEntry[] getGames() {
        return games;
    }

    public void setGames(TestListEntry[] games) {
        this.games = games;
    }
}
