package service;

import service.model.CreateGameRequest;
import service.model.CreateGameResult;
import service.model.JoinGameRequest;
import service.model.ListGamesResult;


public class GameService {
    private static Integer nextGameNumber = 0;

    public static ListGamesResult listGames(String authToken) throws ServiceException {
        throw new ServiceException("implement");
    }

    public static CreateGameResult createGame(CreateGameRequest makeGameRequest) throws ServiceException {
        throw new ServiceException("implement");
    }

    public static void joinGame(JoinGameRequest joinGameRequest) throws ServiceException {
        throw new ServiceException("implement");
    }

    public static void changeNextGameNumber(int new_number) {
        nextGameNumber = new_number;
    }
}
