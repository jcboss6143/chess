package dataaccess;

import model.GameData;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDataAccess {
    private static final Map<Integer, GameData> gameInfo = new HashMap<>();

    public static void clear() throws DataAccessException{
        gameInfo.clear();
    }

    public static void addGame(GameData newGame) throws DataAccessException {
        gameInfo.put(newGame.gameID(), newGame);
    }

    public static GameData getGame(Integer gameID) throws DataAccessException{
        return gameInfo.get(gameID);
    }

    public static Collection<GameData> listGames() throws DataAccessException{
        return gameInfo.values();
    }

    public static void updateGame(GameData game) throws DataAccessException{
        gameInfo.replace(game.gameID(), game);
    }

}
