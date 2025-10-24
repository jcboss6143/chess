package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class GameAccessSQL implements GameAccess{

    public GameAccessSQL() throws DataAccessException {
        buildGameDatabase();
    }

    public void clear() throws DataAccessException{

    }

    public void addGame(GameData newGame) throws DataAccessException {

    }

    public GameData getGame(Integer gameID) throws DataAccessException{
        throw new DataAccessException("implement!");
    }

    public Collection<GameData> listGames() throws DataAccessException{
        throw new DataAccessException("implement!");
    }

    public void updateGame(GameData game) throws DataAccessException{
    }

    private void buildGameDatabase() throws DataAccessException{

    }
}
