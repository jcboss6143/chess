package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.CommonServices;
import service.GameService;
import service.ServiceException;
import service.UserService;
import service.model.CreateGameRequest;
import service.model.JoinGameRequest;
import service.model.LoginRequest;
import java.util.Map;


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
        server.put("game", this::joinGame);
        server.exception(Exception.class, this::exceptionHandler);
        server.error(404, this::notFoundError);
    }



    // =============== Error Handling  =============== //

    private void exceptionHandler(Exception e, Context context) {
        String errorMessage = e.getMessage();
        switch (errorMessage) {
            case "400" -> formatError("bad request", context, 400);
            case "401" -> formatError("unauthorized", context, 401);
            case "403" -> formatError("already taken", context, 403);
            case null, default -> formatError(errorMessage, context, 500);
        }
    }

    private void formatError(String errorMessage, Context context, int statusNumber) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", errorMessage)));
        context.status(statusNumber);
        context.result(body);
    }

    private void notFoundError(Context context) {
        String msg = String.format("[%s] %s not found", context.method(), context.path());
        formatError(msg, context, 404);
    }



    // =============== Service Callers =============== //

    private <T,R> void serviceCaller(Context ctx, T recordObject, ServiceMethod<T, R> serviceMethod)
            throws ServiceException, DataAccessException {
        // passes object to the desired Service Method
        R responseData = serviceMethod.apply(recordObject);
        // Converts result to json and sends result to client
        String json = new Gson().toJson(responseData);
        ctx.result(json);
    }

    private <T> void serviceCaller(Context ctx, T recordObject, VoidServiceMethod<T> serviceMethod)
            throws ServiceException, DataAccessException {
        serviceMethod.apply(recordObject);
        ctx.result("{}");
    }

    // need these functional interfaces to handle errors thrown by the Service Methods passed into service Caller
    @FunctionalInterface
    public interface ServiceMethod<T, R> { R apply(T t) throws ServiceException, DataAccessException; }

    @FunctionalInterface
    public interface VoidServiceMethod<T> { void apply(T t) throws ServiceException, DataAccessException; }



    // =============== Input Formatting =============== //

    private void register(Context ctx) throws ServiceException, DataAccessException  {
        UserData registerData = new Gson().fromJson(ctx.body(), UserData.class);
        serviceCaller(ctx, registerData, UserService::register);
    }

    private void login(Context ctx) throws ServiceException, DataAccessException  {
        LoginRequest loginData = new Gson().fromJson(ctx.body(), LoginRequest.class);
        serviceCaller(ctx, loginData, UserService::login);
    }

    private void logout(Context ctx) throws ServiceException, DataAccessException  {
        serviceCaller(ctx, ctx.header("authorization"), UserService::logout);
    }

    private void listGames(Context ctx) throws ServiceException, DataAccessException  {
        serviceCaller(ctx, ctx.header("authorization"), GameService::listGames);
    }

    private void createGame(Context ctx) throws ServiceException, DataAccessException  {
        CreateGameRequest gameName = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameRequest createGameObject = new CreateGameRequest(ctx.header("authorization"), gameName.gameName());
        serviceCaller(ctx, createGameObject, GameService::createGame);
    }

    private void joinGame(Context ctx) throws ServiceException, DataAccessException  {
        JoinGameRequest gameInfo = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
        JoinGameRequest joinGameObject = new JoinGameRequest(ctx.header("authorization"), gameInfo.playerColor(), gameInfo.gameID());
        serviceCaller(ctx, joinGameObject, GameService::joinGame);
    }

    private void clearApplication(Context ctx) throws DataAccessException {
        CommonServices.deleteAllData();
        ctx.result("{}");
    }



    // =============== Run and Stop  =============== //

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
