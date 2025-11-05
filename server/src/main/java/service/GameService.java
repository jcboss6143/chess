package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.JoinGameRequest;
import model.ListGamesResult;

import java.util.Collection;
import java.util.Objects;


public class GameService {
    private final CommonServices commonServices;
    private final GameAccess gameAccess;
    private final AuthAccess authAccess;

//    private Integer nextGameNumber = 1001;

    public GameService(CommonServices commonServices, GameAccess gameAccess, AuthAccess authAccess) {
        this.commonServices = commonServices;
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    public ListGamesResult listGames(String authToken) throws ServiceException, DataAccessException {
        commonServices.getAndVerifyAuthData(authToken);
        Collection<GameData> gameData = gameAccess.listGames();
        return new ListGamesResult(gameData);
    }

    public CreateGameResult createGame(CreateGameRequest makeGameRequest) throws ServiceException, DataAccessException {
        if (makeGameRequest.gameName() == null){ throw new ServiceException("400"); } // can't have empty game name
        commonServices.getAndVerifyAuthData(makeGameRequest.authToken());
        GameData newGame = new GameData(0, null, null, makeGameRequest.gameName(), new ChessGame());
        int id = gameAccess.addGame(newGame);
        return new CreateGameResult(id);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws ServiceException, DataAccessException {
        if (joinGameRequest.playerColor() == null){ throw new ServiceException("400"); }
        commonServices.getAndVerifyAuthData(joinGameRequest.authToken());
        GameData gameToJoin = gameAccess.getGame(joinGameRequest.gameID());
        String color = joinGameRequest.playerColor();
        if (gameToJoin == null) { throw new ServiceException("400"); } // thrown if game doesn't exist
        String username = authAccess.getAuthData(joinGameRequest.authToken()).username();
        if (Objects.equals(color, "BLACK")) {
            updateGame(gameToJoin, gameToJoin.blackUsername(), gameToJoin.whiteUsername(), username);
        } else if (Objects.equals(color, "WHITE")) {
            updateGame(gameToJoin, gameToJoin.whiteUsername(), username, gameToJoin.blackUsername());
        } else {
            throw new ServiceException("400"); } // request didn't have a valid player color
    }

    private void updateGame(GameData gameToJoin, String playerSeat, String whiteSeat, String blackSeat)
            throws ServiceException, DataAccessException {
        if (playerSeat != null) { throw new ServiceException("403"); } // the seat the player wanted to take is already taken
        GameData updatedGame = new GameData(gameToJoin.gameID(), whiteSeat, blackSeat, gameToJoin.gameName(), gameToJoin.game());
        gameAccess.updateGame(updatedGame);
    }

}
