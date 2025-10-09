package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.ServiceException;
import service.UserService;
import java.util.Map;
import java.util.Objects;


public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", this::clearApplication);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("games", this::joinGame);

        server.exception(Exception.class, this::exceptionHandler);
        server.error(404, this::notFoundError);
        server.start(8080);
        // Register your endpoints and exception handlers here.
    }

    private void formatError(String errorMessage, Context context, int statusNumber) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", errorMessage)));
        context.status(statusNumber);
        context.json(body);
    }

    private void exceptionHandler(Exception e, Context context) {
        String errorMessage = e.getMessage();
        switch (errorMessage) {
            case "400" -> formatError("bad request", context, 400);
            case "401" -> formatError("unauthorized", context, 401);
            case "403" -> formatError("already taken", context, 403);
            case null, default -> formatError(errorMessage, context, 500);
        }
    }

    private void notFoundError(Context context) {
        String msg = String.format("[%s] %s not found", context.method(), context.path());
        formatError(msg, context, 404);
    }



    private void clearApplication(Context ctx) throws ServiceException, DataAccessException {
//        service.DatabaseService.deleteAllData();
        ctx.result("{}");
    }


    private void register(Context ctx) throws ServiceException, DataAccessException  {
        // register user
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        AuthData authData = UserService.register(userData);
        // Convert authData to json and send to client
        String json = new Gson().toJson(authData);
        ctx.result(json);
    }

    private void login(Context ctx) throws ServiceException, DataAccessException  {

    }

    private void logout(Context ctx) throws ServiceException, DataAccessException  {

    }

    private void listGames(Context ctx) throws ServiceException, DataAccessException  {

    }

    private void createGame(Context ctx) throws ServiceException, DataAccessException  {

    }

    private void joinGame(Context ctx) throws ServiceException, DataAccessException  {

    }



    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
