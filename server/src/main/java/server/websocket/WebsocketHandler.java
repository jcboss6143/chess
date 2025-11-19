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
import model.AuthData;
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
            case MAKE_MOVE -> makeMoveCommand(command.getAuthToken(), command.getGameID(), ctx.session, command.getMove());
            case LEAVE -> leaveCommand(command.getAuthToken(), command.getGameID(), ctx.session);
            case RESIGN -> resignCommand(command.getAuthToken(), command.getGameID(), ctx.session);
        }
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


    private void connectCommand(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        AuthData authInfo = userService.getUserInfoFromAuth(authToken);
        GameData gameInfo = gameService.getGame(gameID);
        if (validateRequest(authInfo, gameInfo, gameID, session)) { return; }

        String username = authInfo.username();

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


    private void makeMoveCommand(String authToken, Integer gameID, Session session, ChessMove move) throws DataAccessException, IOException {
        AuthData authInfo = userService.getUserInfoFromAuth(authToken);
        GameData gameInfo = gameService.getGame(gameID);
        if (validateRequest(authInfo, gameInfo, gameID, session)) { return; }
        if (gameInfo.game().isGameFinished()) {
            broadcastError(gameID, session, "Error: Game Is Finished");
            return;
        }

        String username = authInfo.username();
        ChessGame game = gameInfo.game();
        ChessGame.TeamColor teamTurn = game.getTeamTurn();

        // testing if the move is valid or not
        if (!Objects.equals(gameInfo.blackUsername(), username) && !Objects.equals(gameInfo.whiteUsername(), username)) {
            broadcastError(gameID, session, "Error: Unauthorized");
            return;
        }
        boolean isBlack = Objects.equals(gameInfo.blackUsername(), username);
        if (teamTurn == ChessGame.TeamColor.WHITE && isBlack){
            broadcastError(gameID, session, "Error: Unauthorized");
            return;
        }
        if (teamTurn == ChessGame.TeamColor.BLACK && !isBlack){
            broadcastError(gameID, session, "Error: Unauthorized");
            return;
        }
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            broadcastError(gameID, session, "Error: Invalid Move");
            return;
        }

        String playerColor;
        if (isBlack) { playerColor = "black"; }
        else { playerColor = "white"; }
        // setup so we can broadcast the move that was made
        String startPos = String.format("%c%d", 96+move.getStartPosition().getRow(), move.getStartPosition().getColumn());
        String endPos = String.format("%c%d", 96+move.getEndPosition().getRow(), move.getEndPosition().getColumn());
        String moveMessage = String.format("%s (%s) moved %s to %s ", username, playerColor, startPos, endPos);


        // setup so we can broadcast if the player is in check, checkmate, or stalemate
        boolean playerInTrubble = false;
        String checkMessage = "";
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            checkMessage = String.format("%s (white) is in checkmate ", gameInfo.whiteUsername());
            playerInTrubble = true;
        }
        else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            checkMessage = String.format("%s (black) is in checkmate ", gameInfo.blackUsername());
            playerInTrubble = true;
        }
        else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            checkMessage = String.format("%s (white) is in stalemate ", gameInfo.whiteUsername());
            playerInTrubble = true;
        }
        else if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            checkMessage = String.format("%s (black) is in stalemate ", gameInfo.blackUsername());
            playerInTrubble = true;
        }
        else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            checkMessage = String.format("%s (white) is in check ", gameInfo.whiteUsername());
            playerInTrubble = true;
        }
        else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            checkMessage = String.format("%s (black) is in check ", gameInfo.blackUsername());
            playerInTrubble = true;
        }

        // updating game and broadcasting our messages
        GameData updatedGame = new GameData(gameInfo.gameID(), gameInfo.whiteUsername(), gameInfo.blackUsername(), gameInfo.gameName(), game);
        gameService.updateGame(updatedGame);
        LoadGameMessage loadGame = new LoadGameMessage(updatedGame);
        connections.broadcast(gameID, null, loadGame, false); // load game message

        if (playerInTrubble) {  // check message
            NotificationMessage checkNotification = new NotificationMessage(moveMessage + "\n" + checkMessage);
            connections.broadcast(gameID, session, checkNotification, true);
        }
        else {
            NotificationMessage moveNotification = new NotificationMessage(moveMessage);
            connections.broadcast(gameID, session, moveNotification, true); // move message
        }
    }


    private void leaveCommand(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        AuthData authInfo = userService.getUserInfoFromAuth(authToken);
        GameData gameInfo = gameService.getGame(gameID);
        if (validateRequest(authInfo, gameInfo, gameID, session)) { return; }

        String username = authInfo.username();

        if (Objects.equals(gameInfo.blackUsername(), username)) {
            gameInfo = new GameData(gameInfo.gameID(), gameInfo.whiteUsername(), null, gameInfo.gameName(), gameInfo.game());
            gameService.updateGame(gameInfo);
        }
        if (Objects.equals(gameInfo.whiteUsername(), username)) {
            gameInfo = new GameData(gameInfo.gameID(), null, gameInfo.blackUsername(), gameInfo.gameName(), gameInfo.game());
            gameService.updateGame(gameInfo);
        }

        String message = String.format("%s left the game", username);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification, true);
        connections.remove(gameID, session);
    }


    private void resignCommand(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        AuthData authInfo = userService.getUserInfoFromAuth(authToken);
        GameData gameInfo = gameService.getGame(gameID);
        if (validateRequest(authInfo, gameInfo, gameID, session)) { return; }
        if (gameInfo.game().isGameFinished()) {
            broadcastError(gameID, session, "Error: Game Is Finished");
            return;
        }

        String username = authInfo.username();

        if (!Objects.equals(gameInfo.blackUsername(), username) && !Objects.equals(gameInfo.whiteUsername(), username)) {
            broadcastError(gameID, session, "Error: Unauthorized");
            return;
        }

        gameInfo.game().playerResigned();
        gameService.updateGame(gameInfo);

        String resigner;
        if (Objects.equals(gameInfo.blackUsername(), username)) { resigner = "black"; }
        else { resigner = "white"; }
        String message = String.format("%s (%s) has resigned", username, resigner);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, null, notification, false);
    }


    private void broadcastError(Integer gameID, Session session, String message) throws IOException {
        ErrorMessage errorMsg = new ErrorMessage(message);
        connections.broadcast(gameID, session, errorMsg, false);
    }


    private boolean validateRequest(AuthData authInfo, GameData gameInfo, Integer gameID, Session session) throws IOException {
        if (gameInfo == null || authInfo == null) {
            broadcastError(gameID, session, "Error: Invalid request");
            return true;
        }
        return false;
    }
}
