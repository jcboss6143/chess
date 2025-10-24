package dataaccess;

import model.GameData;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDataAccess implements GameAccess{
    private final Map<Integer, GameData> GAME_INFO;

    public GameDataAccess() {
        GAME_INFO = new HashMap<>();
    }

    public void clear() throws DataAccessException{
        GAME_INFO.clear();
    }

    public void addGame(GameData newGame) throws DataAccessException {
        GAME_INFO.put(newGame.gameID(), newGame);
    }

    public GameData getGame(Integer gameID) throws DataAccessException{
        return GAME_INFO.get(gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException{
        return GAME_INFO.values();
    }

    public void updateGame(GameData game) throws DataAccessException{
        GAME_INFO.replace(game.gameID(), game);
    }

}
