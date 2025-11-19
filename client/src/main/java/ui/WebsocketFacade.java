package ui;

import chess.ChessMove;
import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;
    String authToken;
    Integer gameID;

    public WebsocketFacade(String url, ServerMessageHandler serverMessageHandler, String token, Integer gameID) {
        authToken = token;
        this.gameID = gameID;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);



            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage newMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (newMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                        serverMessageHandler.notifyNotification(notification);
                    } else if (newMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                        ErrorMessage notification = new Gson().fromJson(message, ErrorMessage.class);
                        serverMessageHandler.notifyError(notification);
                    } else if (newMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadGameMessage notification = new Gson().fromJson(message, LoadGameMessage.class);
                        serverMessageHandler.notifyLoadGame(notification);
                    } else {
                        System.out.print("ERROR: unknown message received");
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new BadResponseExeption(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}


    public void sendCommand(UserGameCommand.CommandType commandType, ChessMove move) {
        try {
            var command = new UserGameCommand(commandType, authToken, gameID);
            if (move != null) {
                command.setMove(move);
            }
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new BadResponseExeption(ex.getMessage());
        }
    }

}
