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

public class Server {
    private static final Gson gson = new Gson();
    private UserService userService;
    private GameService gameService;
    private AuthService authService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Initialize DAOs and Services
        userService = new UserService(new UserDAO(), new AuthService(new AuthDAO()));
        gameService = new GameService(new GameDAO());
        authService = new AuthService(new AuthDAO());

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
            try {
                AuthData authData = userService.register(user);
                res.status(200);
                return gson.toJson(authData);
            } catch (DataAccessException e) {
                res.status(400); // Bad request if there’s an issue
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
        });

        // Login User
        Spark.post("/session", (req, res) -> {
            UserData user = gson.fromJson(req.body(), UserData.class);
            try {
                AuthData authData = userService.login(user);
                res.status(200);
                return gson.toJson(authData);
            } catch (DataAccessException e) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });

        // Logout User
        Spark.delete("/session", (req, res) -> {
            String authToken = req.headers("authorization");
            try {
                userService.logout(authToken);
                res.status(200);
                return gson.toJson(new EmptyResponse());
            } catch (DataAccessException e) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });

        // List Games
        Spark.get("/game", (req, res) -> {
            String authToken = req.headers("authorization");
            if (authService.isValidAuthToken(authToken)) {
                var games = gameService.listGames();
                res.status(200);
                return gson.toJson(games);
            } else {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });

        // Create Game
        Spark.post("/game", (req, res) -> {
            String authToken = req.headers("authorization");
            if (authService.isValidAuthToken(authToken)) {
                var body = gson.fromJson(req.body(), GameRequest.class);
                GameData game = gameService.createGame(body.gameName(), authService.getAuth(authToken).username());
                res.status(200);
                return gson.toJson(game);
            } else {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });

        // Join Game
        Spark.put("/game", (req, res) -> {
            String authToken = req.headers("authorization");
            if (authService.isValidAuthToken(authToken)) {
                var body = gson.fromJson(req.body(), JoinGameRequest.class);
                try {
                    gameService.joinGame(body.gameID(), body.playerColor(), authService.getAuth(authToken).username());
                    res.status(200);
                    return gson.toJson(new EmptyResponse());
                } catch (DataAccessException e) {
                    res.status(400); // Bad request
                    return gson.toJson(new ErrorResponse("Error: bad request"));
                }
            } else {
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
