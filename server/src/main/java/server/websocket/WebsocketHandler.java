package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.CommonServices;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;


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
        String username = userService.getUsernameFromAuth(authToken);
        GameData gameInfo = gameService.getGame(gameID);
        if (gameInfo == null || username == null) {
            broadcastError(gameID, session, "Error: Invalid request");
            return;
        }

        connections.add(gameID, session);

        String joinedAs;
        if (Objects.equals(gameInfo.blackUsername(), username)) { joinedAs = "black"; }
        else if (Objects.equals(gameInfo.whiteUsername(), username)) { joinedAs = "white"; }
        else { joinedAs = "an observer"; }

        String message = String.format("%s joined the game as %S", username, joinedAs);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification, true);

        LoadGameMessage loadGame = new LoadGameMessage(gameInfo);
        connections.broadcast(gameID, session, loadGame, false);
    }

    private void makeMoveCommand(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        String username = userService.getUsernameFromAuth(authToken);
        GameData gameInfo = gameService.getGame(gameID);
        if (gameInfo == null || username == null) {
            broadcastError(gameID, session, "Error: Invalid request");
            return;
        }

        ChessGame game = gameInfo.game();

        if (!Objects.equals(gameInfo.blackUsername(), username) && !Objects.equals(gameInfo.whiteUsername(), username)) {
            broadcastError(gameID, session, "Error: Unauthorized");
            return;
        }

        if (game.getTeamTurn() == ChessGame.TeamColor.WHITE && !Objects.equals(gameInfo.whiteUsername(), username)){
            broadcastError(gameID, session, "Error: Unauthorized");
            return;
        }

        if (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !Objects.equals(gameInfo.blackUsername(), username)){
            broadcastError(gameID, session, "Error: Unauthorized");
            return;
        }

        try {
            game.makeMove(null);
        } catch (InvalidMoveException e) {
            broadcastError(gameID, session, "Error: Invalid Move");
            return;
        }

        GameData updatedGame = new GameData(gameInfo.gameID(), gameInfo.whiteUsername(), gameInfo.blackUsername(), gameInfo.gameName(), game);
        gameService.updateGame(updatedGame);

        LoadGameMessage loadGame = new LoadGameMessage(updatedGame);
        connections.broadcast(gameID, null, loadGame, false);



    }

    private void leaveCommand(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        String username = userService.getUsernameFromAuth(authToken);
        GameData gameInfo = gameService.getGame(gameID);
        if (gameInfo == null || username == null) {
            broadcastError(gameID, session, "Error: Invalid request");
            return;
        }

        String message = String.format("%s left the game", username);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification, true);
        connections.remove(gameID, session);
    }

    private void resignCommand(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        String username = userService.getUsernameFromAuth(authToken);
        GameData gameInfo = gameService.getGame(gameID);
        if (gameInfo == null || username == null) {
            broadcastError(gameID, session, "Error: Invalid request");
            return;
        }

        if (!Objects.equals(gameInfo.blackUsername(), username) && !Objects.equals(gameInfo.whiteUsername(), username)) {
            broadcastError(gameID, session, "Error: Unauthorized");
            return;
        }

        gameInfo.game().playerResigned();

        String resigner;
        if (Objects.equals(gameInfo.blackUsername(), username)) { resigner = "black"; }
        else { resigner = "white"; }
        String message = String.format("%s (%s) has resigned", username, resigner);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification, true);
    }


    private void broadcastError(Integer gameID, Session session, String message) throws IOException {
        ErrorMessage errorMsg = new ErrorMessage(message);
        connections.broadcast(gameID, session, errorMsg, false);
    }
}
