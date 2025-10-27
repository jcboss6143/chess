package DataAccessTests;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.util.Collection;

public class GameAccessTests implements CommonAccessSQL {

    private final GameAccess gameAccess = new GameAccessSQL();

    public GameAccessTests() throws DataAccessException {

    }

    @Test
    @DisplayName("create new game")
    public void clearSuccess() throws DataAccessException {
        gameAccess.clear();
        String statement = "SELECT * from gameData";
        String errorMessage = "Failed";
        boolean tableCleared = sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            try (var rs = preparedStatement.executeQuery()) {
                return !rs.next();
            }
        });
        Assertions.assertTrue(tableCleared);
    }


    @Test
    @DisplayName("gets valid game data")
    public void createGameSuccess() throws DataAccessException {
        gameAccess.clear();
        int gameID = gameAccess.addGame(new GameData(1, null, null, "new_Game"));
        GameData returnedResult = gameAccess.getGame(gameID);
        String statement = "SELECT * from gameData WHERE gameID=" + gameID;
        String errorMessage = "Failed";
        boolean correctOutput = sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Assertions.assertNull(rs.getString("whiteUsername"));
                    Assertions.assertNull(rs.getString("blackUsername"));
                    Assertions.assertEquals("new_Game", rs.getString("gameName"));
                    return true;
                }
                return false;
            }
        });
        Assertions.assertTrue(correctOutput);
    }

    @Test
    @DisplayName("fetch bad game")
    public void GetUserFail() throws DataAccessException {
        gameAccess.clear();
        Assertions.assertNull(gameAccess.getGame(0));
    }


    @Test
    @DisplayName("gets valid game data")
    public void GetGameDataSuccess() throws DataAccessException {
        gameAccess.clear();
        int gameID = gameAccess.addGame(new GameData(1, null, null, "new_Game"));
        GameData returnedResult = gameAccess.getGame(gameID);
        Assertions.assertNull(returnedResult.blackUsername());
        Assertions.assertNull(returnedResult.whiteUsername());
        Assertions.assertEquals("new_Game", returnedResult.gameName());
    }


    @Test
    @DisplayName("gets valid game data")
    public void ListGameSuccess() throws DataAccessException {
        gameAccess.clear();
        int gameID1 = gameAccess.addGame(new GameData(1, null, null, "new_Game1"));
        int gameID2 = gameAccess.addGame(new GameData(1, "joe", null, "new_Game2"));
        int gameID3 = gameAccess.addGame(new GameData(1, null, "jack", "new_Game3"));
        Collection<GameData> returnedResult = gameAccess.listGames();
//        for ()
//        Assertions.assertNull(returnedResult.blackUsername());
//        Assertions.assertNull(returnedResult.whiteUsername());
//        Assertions.assertEquals("new_Game", returnedResult.gameName());
//        Assertions.assertNull(returnedResult.blackUsername());
//        Assertions.assertNull(returnedResult.whiteUsername());
//        Assertions.assertEquals("new_Game", returnedResult.iterator().next().gameName());
    }
}
