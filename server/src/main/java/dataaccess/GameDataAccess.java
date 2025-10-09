package dataaccess;

import model.GameData;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDataAccess {
    private static final Map<Integer, GameData> gameInfo = new HashMap<>();
    private static Integer nextGameNumber = 0;

    public static void clear() throws DataAccessException{
        gameInfo.clear();
        nextGameNumber = 1;
    }

    public void createGame(GameData newGame) throws DataAccessException {
        gameInfo.put(newGame.gameID(), newGame);
    }

    public GameData getGame(Integer gameID) throws DataAccessException{
        return gameInfo.get(gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException{
        return gameInfo.values();
    }

    public void updateGame(GameData game) throws DataAccessException{
        gameInfo.replace(game.gameID(), game);
    }

    public int getGameID() {
        nextGameNumber += 1;
        return nextGameNumber;
    }
}
