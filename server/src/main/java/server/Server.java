package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.UserData;
import service.ClearService;
import service.CreateUser;
import service.CreateGameService;
import service.ListGamesService;
import service.JoinGameService;
import service.LoginService;
import service.LogoutService;

import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();
    private final ClearService clearService;
    private final CreateUser createUserService;
    private final CreateGameService createGameService;
    private final ListGamesService listGamesService;
    private final JoinGameService joinGameService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database not initialized: " + e.getMessage());
        }
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        clearService = new ClearService(dataAccess);
        createUserService = new CreateUser(dataAccess);
        createGameService = new CreateGameService(dataAccess);
        listGamesService = new ListGamesService(dataAccess);
        joinGameService = new JoinGameService(dataAccess);
        loginService = new LoginService(dataAccess);
        logoutService = new LogoutService(dataAccess);
        webSocketHandler = new WebSocketHandler(dataAccess);
        registerEndpoints();
    }

    private String getAuthToken(Context ctx) {
        String authToken = ctx.header("authToken");
        if (authToken == null || authToken.isBlank()) {
            authToken = ctx.header("authorization");
        }
        return authToken;
    }

    private void error(Context ctx, int status, String message) {
        ctx.status(status).contentType("application/json")
                .result(gson.toJson(Map.of("message", "Error: " + message)));
    }

    private String getMsgLower(DataAccessException e) {
        return e.getMessage() == null ? "" : e.getMessage().toLowerCase();
    }

    private int getUnauthorizedOrMissingStatus(String msg) {
        if (msg.contains("unauthorized")) {
            return 401;
        } else if (msg.contains("missing")) {
            return 400;
        }
        return 500;
    }

    private int getJoinGameStatus(String msg) {
        if (msg.contains("unauthorized")) {
            return 401;
        } else if (msg.contains("missing") || msg.contains("invalid") || msg.contains("not found")) {
            return 400;
        } else if (msg.contains("taken")) {
            return 403;
        }
        return 500;
    }

    private void registerEndpoints() {
        javalin.delete("/db", this::handleClear);
        javalin.post("/user", this::handleRegister);
        javalin.post("/game", this::handleCreateGame);
        javalin.get("/game", this::handleListGames);
        javalin.put("/game", this::handleJoinGame);
        javalin.post("/session", this::handleLogin);
        javalin.delete("/session", this::handleLogout);
        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler::onConnect);
            ws.onMessage(webSocketHandler::onMessage);
            ws.onClose(webSocketHandler::onClose);
        });
    }

    private void handleClear(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200).contentType("application/json").result("{}");
        } catch (DataAccessException e) {
            error(ctx, 500, e.getMessage());
        }
    }

    private void handleRegister(Context ctx) {
        try {
            UserData request = gson.fromJson(ctx.body(), UserData.class);
            var auth = createUserService.register(request);
            ctx.status(200).contentType("application/json").result(gson.toJson(auth));
        } catch (DataAccessException e) {
            String msg = getMsgLower(e);
            int status;
            if (msg.contains("missing")) {
                status = 400;
            } else if (msg.contains("already taken") || msg.contains("taken")) {
                status = 403;
            } else {
                status = 500;
            }
            error(ctx, status, e.getMessage());
        }
    }

    private void handleCreateGame(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            CreateGameService.CreateGameRequest request =
                    gson.fromJson(ctx.body(), CreateGameService.CreateGameRequest.class);
            var result = createGameService.createGame(authToken, request);
            ctx.status(200).contentType("application/json").result(gson.toJson(result));
        } catch (DataAccessException e) {
            error(ctx, getUnauthorizedOrMissingStatus(getMsgLower(e)), e.getMessage());
        }
    }

    private void handleListGames(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            var result = listGamesService.listGames(authToken);
            ctx.status(200).contentType("application/json").result(gson.toJson(result));
        } catch (DataAccessException e) {
            int status = getMsgLower(e).contains("unauthorized") ? 401 : 500;
            error(ctx, status, e.getMessage());
        }
    }

    private void handleJoinGame(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            JoinGameService.JoinGameRequest request =
                    gson.fromJson(ctx.body(), JoinGameService.JoinGameRequest.class);
            joinGameService.joinGame(authToken, request);
            ctx.status(200).contentType("application/json").result("{}");
        } catch (DataAccessException e) {
            error(ctx, getJoinGameStatus(getMsgLower(e)), e.getMessage());
        }
    }

    private void handleLogin(Context ctx) {
        try {
            LoginService.LoginRequest request =
                    gson.fromJson(ctx.body(), LoginService.LoginRequest.class);
            var result = loginService.login(request);
            ctx.status(200).contentType("application/json").result(gson.toJson(result));
        } catch (DataAccessException e) {
            error(ctx, getUnauthorizedOrMissingStatus(getMsgLower(e)), e.getMessage());
        }
    }

    private void handleLogout(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            logoutService.logout(authToken);
            ctx.status(200).contentType("application/json").result("{}");
        } catch (DataAccessException e) {
            int status = getMsgLower(e).contains("unauthorized") ? 401 : 500;
            error(ctx, status, e.getMessage());
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}