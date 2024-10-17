package server;

import spark.Spark;
import service.UserService;
import service.GameService;
import service.AuthService;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import com.google.gson.Gson;
import model.UserData;
import model.AuthData;
import model.GameData;
import dataaccess.DataAccessException;
import exceptions.UserAlreadyExistsException;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidRequestException;
import exceptions.InvalidAuthTokenException;
import exceptions.PlayerSpotTakenException;
import exceptions.InvalidPlayerColorException;
import exceptions.GameNotFoundException;

import java.util.Map;

public class Server {
    private static final Gson gson = new Gson();
    private UserService userService;
    private GameService gameService;
    private AuthService authService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Initialize DAOs and Services
        AuthDAO sharedAuthDAO = new AuthDAO();
        authService = new AuthService(sharedAuthDAO);
        userService = new UserService(new UserDAO(), authService);
        gameService = new GameService(new GameDAO());

        // Clear Application Data
        Spark.delete("/db", (req, res) -> {
            userService.clear();
            gameService.clear();
            authService.clear();
            res.status(200);
            return gson.toJson(new EmptyResponse());
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
                if (!authService.isValidAuthToken(authToken)) {
                    res.status(401); // Unauthorized
                    return gson.toJson(new ErrorResponse("Error: unauthorized"));
                }

                var games = gameService.listGames();

                // Convert games to TestListEntry array
                TestListEntry[] gameEntries = games.values().stream()
                        .map(game -> new TestListEntry(
                                game.gameID(),
                                game.gameName(),
                                game.whiteUsername(),
                                game.blackUsername()))
                        .toArray(TestListEntry[]::new);

                // Create and return the result
                TestListResult listResult = new TestListResult();
                listResult.setGames(gameEntries);
                res.status(200);
                return gson.toJson(listResult);
            } catch (InvalidAuthTokenException e) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
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
                if (!authService.isValidAuthToken(authToken)) {
                    res.status(401); // Unauthorized
                    return gson.toJson(new ErrorResponse("Error: unauthorized"));
                }
                var body = gson.fromJson(req.body(), GameRequest.class);
                if (body.gameName() == null) {
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
                if (!authService.isValidAuthToken(authToken)) {
                    res.status(401); // Unauthorized
                    return gson.toJson(new ErrorResponse("Error: unauthorized"));
                }
                var body = gson.fromJson(req.body(), JoinGameRequest.class);
                if (body.playerColor() == null || body.gameID() == 0) {
                    throw new InvalidRequestException("Missing player color or game ID.");
                }
                gameService.joinGame(body.gameID(), body.playerColor(), authService.getAuth(authToken).username());
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
            }
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
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

// New Classes for Test Responses

class TestCreateResult {
    private int gameID;

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
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

    // Getters and setters if needed
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
