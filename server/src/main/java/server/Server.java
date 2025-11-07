package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.CommonServices;
import service.GameService;
import service.ServiceException;
import service.UserService;
import model.CreateGameRequestS;
import model.JoinGameRequestS;
import model.LoginRequest;
import java.util.Map;


public class Server {

    private final Javalin server;
    private final CommonServices commonServices;
    private final UserService userService;
    private final GameService gameService;

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
        try {
            AuthAccess authAccess = new AuthAccessSQL();
            GameAccess gameAccess = new GameAccessSQL();
            UserAccess userAccess = new UserAccessSQL();
            this.commonServices = new CommonServices(authAccess, gameAccess, userAccess);
            this.userService = new UserService(authAccess, userAccess, commonServices);
            this.gameService = new GameService(commonServices, gameAccess, authAccess);
        } catch (Throwable ex) {
            throw new RuntimeException("Database initialization failed", ex);
        }
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
        R responseData = serviceMethod.apply(recordObject); // passes object to desired Service
        String json = new Gson().toJson(responseData);
        ctx.result(json);
    }

    private <T> void serviceCaller(Context ctx, T recordObject, VoidServiceMethod<T> serviceMethod)
            throws ServiceException, DataAccessException {
        serviceMethod.apply(recordObject);
        ctx.result("{}");
    }

    // need these functional interfaces to handle errors thrown by ServiceMethod in serviceCaller
    @FunctionalInterface
    public interface ServiceMethod<T, R> { R apply(T t) throws ServiceException, DataAccessException; }

    @FunctionalInterface
    public interface VoidServiceMethod<T> { void apply(T t) throws ServiceException, DataAccessException; }



    // =============== Input Formatting =============== //

    private void register(Context ctx) throws ServiceException, DataAccessException  {
        if (ctx.body().isEmpty()) { throw new ServiceException("400"); }
        UserData registerData = new Gson().fromJson(ctx.body(), UserData.class);
        serviceCaller(ctx, registerData, userService::register);
    }

    private void login(Context ctx) throws ServiceException, DataAccessException  {
        if (ctx.body().isEmpty()) { throw new ServiceException("400"); }
        LoginRequest loginData = new Gson().fromJson(ctx.body(), LoginRequest.class);
        serviceCaller(ctx, loginData, userService::login);
    }

    private void logout(Context ctx) throws ServiceException, DataAccessException  {
        serviceCaller(ctx, ctx.header("authorization"), userService::logout);
    }

    private void listGames(Context ctx) throws ServiceException, DataAccessException  {
        serviceCaller(ctx, ctx.header("authorization"), gameService::listGames);
    }

    private void createGame(Context ctx) throws ServiceException, DataAccessException  {
        if (ctx.body().isEmpty()) { throw new ServiceException("400"); }
        CreateGameRequestS gameName = new Gson().fromJson(ctx.body(), CreateGameRequestS.class);
        CreateGameRequestS createGameObject = new CreateGameRequestS(ctx.header("authorization"), gameName.gameName());
        serviceCaller(ctx, createGameObject, gameService::createGame);
    }

    private void joinGame(Context ctx) throws ServiceException, DataAccessException  {
        if (ctx.body().isEmpty()) { throw new ServiceException("400"); }
        JoinGameRequestS gameInfo = new Gson().fromJson(ctx.body(), JoinGameRequestS.class);
        JoinGameRequestS joinGameObject = new JoinGameRequestS(ctx.header("authorization"), gameInfo.playerColor(), gameInfo.gameID());
        serviceCaller(ctx, joinGameObject, gameService::joinGame);
    }

    private void clearApplication(Context ctx) throws DataAccessException {
        commonServices.deleteAllData();
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
