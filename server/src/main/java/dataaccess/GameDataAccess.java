package dataaccess;

import model.GameData;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDataAccess {
    private static final Map<Integer, GameData> GameInfo = new HashMap<>();

    public static void clear() throws DataAccessException{
        GameInfo.clear();
    }

    public static void addGame(GameData newGame) throws DataAccessException {
        GameInfo.put(newGame.gameID(), newGame);
    }

    public static GameData getGame(Integer gameID) throws DataAccessException{
        return GameInfo.get(gameID);
    }

    public static Collection<GameData> listGames() throws DataAccessException{
        return GameInfo.values();
    }

    public static void updateGame(GameData game) throws DataAccessException{
        GameInfo.replace(game.gameID(), game);
    }

}
