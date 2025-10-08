package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.ServiceException;
import service.UserService;
import java.util.Map;


public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", this::clearApplication);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/games", this::joinGame);

        javalin.exception(Exception.class, this::serverError)
                .error(404, this::notFoundError)
                .start(8080);
        // Register your endpoints and exception handlers here.
    }

    private void exceptionHandler(Exception e, Context context, int statusNumber) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(statusNumber);
        context.json(body);
    }

    private void serverError(Exception e, Context context) {
        exceptionHandler(e, context, 500);
    }

    private void notFoundError(Context context) {
        String msg = String.format("[%s] %s not found", context.method(), context.path());
        exceptionHandler(new Exception(msg), context, 404);
    }



    private void clearApplication(Context ctx) throws ServiceException {
        service.DatabaseService.deleteAllData();
        ctx.status(200);
    }

    private void register(Context ctx) {
        try{
            // register user
            UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
            AuthData authData = UserService.register(userData);
            // Convert authData to json and send to client
            String json = new Gson().toJson(authData);
            ctx.json(json);
        } catch (ServiceException e) {
            // TODO: Finish implementing all errors
            exceptionHandler(new Exception("unauthorized"), ctx, 401);
        }
    }

    private void login(Context ctx) {

    }

    private void logout(Context ctx) {

    }

    private void listGames(Context ctx) {

    }

    private void createGame(Context ctx) {

    }

    private void joinGame(Context ctx) {

    }



    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
