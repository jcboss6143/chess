package dataaccess;

import chess.ChessGame;
import model.GameData;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDataAccess implements GameAccess{
    private final Map<Integer, GameData> gameInfo;
    private Integer nextGameNumber = 1001;

    public GameDataAccess() {
        gameInfo = new HashMap<>();
    }

    public void clear() throws DataAccessException{
        gameInfo.clear();
    }

    public Integer addGame(GameData newGame) throws DataAccessException {
        nextGameNumber += 1;
        gameInfo.put(nextGameNumber, new GameData(nextGameNumber, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), new ChessGame()));
        return nextGameNumber;
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

}
