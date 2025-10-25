package dataaccess;

import model.GameData;
import model.UserData;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class GameAccessSQL implements GameAccess, CommonAccessSQL{

    public GameAccessSQL() throws DataAccessException {
        buildGameTable();
    }

    public void clear() throws DataAccessException{
        String statement = "DROP TABLE gameData";
        String errorMessage = "Unable to drop table";
        sendStatement(statement, errorMessage, this::executeUpdateCore);
        buildGameTable();
    }



    public Integer addGame(GameData newGame) throws DataAccessException {
        String statement = "INSERT INTO gameData (whiteUsername, blackUsername, gameName) VALUES (?, ?, ?)";
        String errorMessage = "Unable to add gameData";
        return sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            preparedStatement.setString(1, newGame.whiteUsername());
            preparedStatement.setString(2, newGame.blackUsername());
            preparedStatement.setString(3, newGame.gameName());
            preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            var ID = 0;
            if (resultSet.next()) {
                ID = resultSet.getInt(1);
            }
            return ID;
        });
    }



    public GameData getGame(Integer gameID) throws DataAccessException{
        String statement = "SELECT whiteUsername, blackUsername, gameName FROM gameData WHERE id=?";
        String errorMessage = "Unable to fetch gameData";
        return sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            preparedStatement.setInt(1, gameID);
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    return new GameData(gameID, whiteUsername, blackUsername, gameName);
                }
                return null;
            }
        });

    }




    public Collection<GameData> listGames() throws DataAccessException{
        String statement = "SELECT id, whiteUsername, blackUsername, gameName FROM gameData";
        String errorMessage = "Unable to fetch gameData";
        return sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            try (var rs = preparedStatement.executeQuery()) {
                HashSet<GameData> gameSet = new HashSet<GameData>();
                while (rs.next()) {
                    int gameID = rs.getInt("id");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    gameSet.add(new GameData(gameID, whiteUsername, blackUsername, gameName));
                }
                return gameSet;
            }
        });
    }


    public void updateGame(GameData game) throws DataAccessException{
        String statement = "UPDATE gameData SET whiteUsername=?, blackUsername=? WHERE id=?";
        String errorMessage = "Unable to fetch gameData";
        sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setInt(3, game.gameID());
            preparedStatement.executeUpdate();
            return 1;
        });
    }

    private void buildGameTable() throws DataAccessException{
        DatabaseManager.createDatabase();
        String statement = """
                CREATE TABLE IF NOT EXISTS gameData (
                 id INT NOT NULL AUTO_INCREMENT,
                 whiteUsername varchar(256),
                 blackUsername varchar(256),
                 gameName varchar(256),
                PRIMARY KEY (id)  )
            """;
        String errorMessage = "Unable to configure table userData";
        sendStatement(statement, errorMessage, this::executeUpdateCore);
    }
}
