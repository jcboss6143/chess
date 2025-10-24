package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameAccess {
    void clear() throws DataAccessException;
    Integer addGame(GameData newGame) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;

}
