package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;


public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
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

    private void connectCommand(String authToken, Integer gameID, Session session) {

    }

    private void makeMoveCommand(String authToken, Integer gameID, Session session) {

    }

    private void leaveCommand(String authToken, Integer gameID, Session session) {

    }

    private void resignCommand(String authToken, Integer gameID, Session session) {

    }
}
