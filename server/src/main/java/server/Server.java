package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import model.UserData;
import service.ClearService;
import service.CreateUser;

import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final Gson gson = new Gson();
    private final ClearService clearService = new ClearService(dataAccess);
    private final CreateUser createUserService = new CreateUser(dataAccess);

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
                // Common mapping: bad input -> 400, otherwise treat as already taken -> 403
                int status = (e.getMessage() != null && e.getMessage().toLowerCase().contains("missing")) ? 400 : 403;
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
