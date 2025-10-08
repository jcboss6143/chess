package server;

import io.javalin.*;
import io.javalin.http.Context;

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
        // Register your endpoints and exception handlers here.

    }


    private void clearApplication(Context ctx) {

    }

    private void register(Context ctx) {

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
