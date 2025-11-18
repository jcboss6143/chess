package ui;

import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebsocketFacade(String url, ServerMessageHandler serverMessageHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage new_message = new Gson().fromJson(message, ServerMessage.class);
                if (new_message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    serverMessageHandler.notifyNotification(notification);
                }
                if (new_message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                    ErrorMessage notification = new Gson().fromJson(message, ErrorMessage.class);
                    serverMessageHandler.notifyError(notification);
                }
                if (new_message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                    LoadGameMessage notification = new Gson().fromJson(message, LoadGameMessage.class);
                    serverMessageHandler.notifyLoadGame(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new BadResponseExeption(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}


}
