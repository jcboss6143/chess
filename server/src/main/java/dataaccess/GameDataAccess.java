package dataaccess;

import model.GameData;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDataAccess {
    private static final Map<Integer, GameData> GAME_INFO = new HashMap<>();

    public static void clear() throws DataAccessException{
        GAME_INFO.clear();
    }

    public static void addGame(GameData newGame) throws DataAccessException {
        GAME_INFO.put(newGame.gameID(), newGame);
    }

    public static GameData getGame(Integer gameID) throws DataAccessException{
        return GAME_INFO.get(gameID);
    }

    public static Collection<GameData> listGames() throws DataAccessException{
        return GAME_INFO.values();
    }

    public static void updateGame(GameData game) throws DataAccessException{
        GAME_INFO.replace(game.gameID(), game);
    }

}
