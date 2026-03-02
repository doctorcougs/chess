package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import model.UserData;
import service.ClearService;
import service.CreateUser;
import service.CreateGameService;
import service.ListGamesService;
import service.JoinGameService;


import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final Gson gson = new Gson();
    private final ClearService clearService = new ClearService(dataAccess);
    private final CreateUser createUserService = new CreateUser(dataAccess);
    private final CreateGameService createGameService = new CreateGameService(dataAccess);
    private final ListGamesService listGamesService = new ListGamesService(dataAccess);
    private final JoinGameService joinGameService = new JoinGameService(dataAccess);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        registerEndpoints();
    }

    private void registerEndpoints() {
        javalin.delete("/db", ctx -> {
            try {
                clearService.clear();
                ctx.status(200).contentType("application/json").result("{}");
            } catch (DataAccessException e) {
                ctx.status(500).contentType("application/json")
                   .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        javalin.post("/user", ctx -> {
            try {
                UserData request = gson.fromJson(ctx.body(), UserData.class);
                var auth = createUserService.register(request);
                ctx.status(200).contentType("application/json").result(gson.toJson(auth));
            } catch (DataAccessException e) {
                int status = (e.getMessage() != null && e.getMessage().toLowerCase().contains("missing")) ? 400 : 403;
                ctx.status(status).contentType("application/json")
                   .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            } catch (Exception e) {
                ctx.status(500).contentType("application/json")
                   .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        javalin.post("/game", ctx -> {
            try {
                String authToken = ctx.header("authToken");
                if (authToken == null || authToken.isBlank()) {
                    authToken = ctx.header("authorization");
                }

                CreateGameService.CreateGameRequest request =
                        gson.fromJson(ctx.body(), CreateGameService.CreateGameRequest.class);

                var result = createGameService.createGame(authToken, request);
                ctx.status(200).contentType("application/json").result(gson.toJson(result));
            } catch (DataAccessException e) {
                String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                int status = msg.contains("unauthorized") ? 401 : (msg.contains("missing") ? 400 : 500);
                ctx.status(status).contentType("application/json")
                   .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            } catch (Exception e) {
                ctx.status(500).contentType("application/json")
                   .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        javalin.get("/game", ctx -> {
            try {
                String authToken = ctx.header("authToken");
                if (authToken == null || authToken.isBlank()) {
                    authToken = ctx.header("authorization");
                }

                var result = listGamesService.listGames(authToken);
                ctx.status(200).contentType("application/json").result(gson.toJson(result));
            } catch (DataAccessException e) {
                String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                int status = msg.contains("unauthorized") ? 401 : 500;
                ctx.status(status).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            } catch (Exception e) {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        javalin.put("/game", ctx -> {
            try {
                String authToken = ctx.header("authToken");
                if (authToken == null || authToken.isBlank()) {
                    authToken = ctx.header("authorization");
                }

                JoinGameService.JoinGameRequest request =
                        gson.fromJson(ctx.body(), JoinGameService.JoinGameRequest.class);

                joinGameService.joinGame(authToken, request);
                ctx.status(200).contentType("application/json").result("{}");
            } catch (DataAccessException e) {
                String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                int status =
                        msg.contains("unauthorized") ? 401 :
                                (msg.contains("missing") || msg.contains("invalid") || msg.contains("not found")) ? 400 :
                                        msg.contains("taken") ? 403 : 500;

                ctx.status(status).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            } catch (Exception e) {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
