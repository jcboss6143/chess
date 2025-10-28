package DataAccessTests;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class GameAccessTests implements CommonAccessSQL {

    private final GameAccess gameAccess = new GameAccessSQL();

    public GameAccessTests() throws DataAccessException {
    }

    @Test
    @DisplayName("clear game")
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
    @DisplayName("creates valid Game")
    public void createGameSuccess() throws DataAccessException {
        gameAccess.clear();
        int gameID = gameAccess.addGame(new GameData(1, null, null, "new_Game"));
        String statement = "SELECT * from gameData WHERE id=?";
        String errorMessage = "Failed";
        boolean correctOutput = sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            preparedStatement.setInt(1, gameID);
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Assertions.assertNull(rs.getString("whiteUsername"));
                    Assertions.assertNull(rs.getString("blackUsername"));
                    Assertions.assertEquals("new_Game", rs.getString("gameName"));
                    Assertions.assertEquals( new Gson().toJson(new ChessGame()), rs.getString("game"));
                    return true;
                }
                return false;
            }
        });
        Assertions.assertTrue(correctOutput);
    }


    @Test
    @DisplayName("create bad game")
    public void CreateGameFail() throws DataAccessException {
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            gameAccess.addGame(new GameData(1, null, null, null));
        });
        Assertions.assertEquals("Unable to add gameData: Column 'gameName' cannot be null", exception.getMessage());
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
    @DisplayName("gets invalid game data")
    public void GetGameDataFail() throws DataAccessException {
        gameAccess.clear();
        int gameID = gameAccess.addGame(new GameData(3, null, null, "new_Game"));
        Assertions.assertNull(gameAccess.getGame(5));
    }


    @Test
    @DisplayName("gets valid game data list")
    public void ListGameSuccess() throws DataAccessException {
        gameAccess.clear();
        int gameID1 = gameAccess.addGame(new GameData(1, null, null, "new_Game1"));
        int gameID2 = gameAccess.addGame(new GameData(1, null, null, "new_Game2"));
        int gameID3 = gameAccess.addGame(new GameData(1, null, null, "new_Game3"));
        Collection<GameData> returnedResult = gameAccess.listGames();
        int i = 1;
        for (GameData game : returnedResult) {
            Assertions.assertNull(game.blackUsername());
            Assertions.assertNull(game.whiteUsername());
            Assertions.assertEquals("new_Game" + i, game.gameName());
            i += 1;
        }
    }


    @Test
    @DisplayName("list games when there are none")
    public void ListGameFail() throws DataAccessException {
        gameAccess.clear();
        Collection<GameData> returnedResult = gameAccess.listGames();
        Assertions.assertEquals(0, returnedResult.size());
    }


    @Test
    @DisplayName("update game data success")
    public void UpdateGameSuccess() throws DataAccessException {
        gameAccess.clear();
        int gameID = gameAccess.addGame(new GameData(1, null, null, "new_Game"));
        gameAccess.updateGame(new GameData(gameID, "WhiteUser", "BlackUser", "new_Game"));
        GameData returnedResult = gameAccess.getGame(gameID);
        Assertions.assertEquals("WhiteUser", returnedResult.whiteUsername());
        Assertions.assertEquals("BlackUser", returnedResult.blackUsername());
        Assertions.assertEquals("new_Game", returnedResult.gameName());
    }


    @Test
    @DisplayName("bad update game data")
    public void UpdateGameFail() throws DataAccessException {
        gameAccess.clear();
        int gameID = gameAccess.addGame(new GameData(1, null, null, "new_Game"));
        gameAccess.updateGame(new GameData(4, "WhiteUser", "BlackUser", null));
        GameData returnedResult = gameAccess.getGame(gameID);
        Assertions.assertNull(gameAccess.getGame(4));
        Assertions.assertNull(returnedResult.blackUsername());
        Assertions.assertNull(returnedResult.whiteUsername());
        Assertions.assertEquals("new_Game", returnedResult.gameName());
    }


}
