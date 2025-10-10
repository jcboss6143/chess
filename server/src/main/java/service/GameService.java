package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.GameData;
import service.model.CreateGameRequest;
import service.model.CreateGameResult;
import service.model.JoinGameRequest;
import service.model.ListGamesResult;

import java.util.Collection;
import java.util.Objects;


public class GameService {
    private static Integer nextGameNumber = 1001;

    public static ListGamesResult listGames(String authToken) throws ServiceException, DataAccessException {
        CommonServices.getAndVerifyAuthData(authToken);
        Collection<GameData> gameData = GameDataAccess.listGames();
        return new ListGamesResult(gameData);
    }

    public static CreateGameResult createGame(CreateGameRequest makeGameRequest) throws ServiceException, DataAccessException {
        if (makeGameRequest.gameName() == null){ throw new ServiceException("400"); } // can't have empty game name
        CommonServices.getAndVerifyAuthData(makeGameRequest.authToken());
        while (GameDataAccess.getGame(nextGameNumber) != null) { nextGameNumber += 1; } // makes sure we don't have duplicate gameIDs
        GameData newGame = new GameData(nextGameNumber, null, null, makeGameRequest.gameName());
        nextGameNumber += 1;
        GameDataAccess.addGame(newGame);
        return new CreateGameResult(newGame.gameID());
    }

    public static void joinGame(JoinGameRequest joinGameRequest) throws ServiceException, DataAccessException {
        if (joinGameRequest.playerColor() == null){ throw new ServiceException("400"); }
        CommonServices.getAndVerifyAuthData(joinGameRequest.authToken());
        GameData gameToJoin = GameDataAccess.getGame(joinGameRequest.gameID());
        String color = joinGameRequest.playerColor();
        if (gameToJoin == null) { throw new ServiceException("400"); } // thrown if game doesn't exist
        String username = AuthDataAccess.getAuthData(joinGameRequest.authToken()).username();
        if (Objects.equals(color, "BLACK")) {
            updateGame(gameToJoin, gameToJoin.blackUsername(), gameToJoin.whiteUsername(), username);
        } else if (Objects.equals(color, "WHITE")) {
            updateGame(gameToJoin, gameToJoin.whiteUsername(), username, gameToJoin.blackUsername());
        } else {
            throw new ServiceException("400"); } // request didn't have a valid player color
    }

    private static void updateGame(GameData gameToJoin, String playerSeat, String whiteSeat, String blackSeat)
            throws ServiceException, DataAccessException {
        if (playerSeat != null) { throw new ServiceException("403"); } // the seat the player wanted to take is already taken
        GameData updatedGame = new GameData(gameToJoin.gameID(), whiteSeat, blackSeat, gameToJoin.gameName());
        GameDataAccess.updateGame(updatedGame);
    }

    public static void changeNextGameNumber(int new_number) {
        nextGameNumber = new_number;
    }
}
