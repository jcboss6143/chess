package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import service.CommonServices;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final CommonServices commonServices;
    private final UserService userService;
    private final GameService gameService;

    public WebsocketHandler(CommonServices common, UserService user, GameService game){
        super();
        commonServices = common;
        userService = user;
        gameService = game;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws DataAccessException, IOException {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connectCommand(command.getAuthToken(), command.getGameID(), ctx.session);
            case MAKE_MOVE -> makeMoveCommand(command.getAuthToken(), command.getGameID(), ctx.session);
            case LEAVE -> leaveCommand(command.getAuthToken(), command.getGameID(), ctx.session);
            case RESIGN -> resignCommand(command.getAuthToken(), command.getGameID(), ctx.session);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connectCommand(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        connections.add(gameID, session);
        String username = userService.getUsernameFromAuth(authToken);

        var message = String.format("%s joined the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification, true);
    }

    private void makeMoveCommand(String authToken, Integer gameID, Session session) {

    }

    private void leaveCommand(String authToken, Integer gameID, Session session) {

    }

    private void resignCommand(String authToken, Integer gameID, Session session) {

    }
}
