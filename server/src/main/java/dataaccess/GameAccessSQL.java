package dataaccess;

import model.GameData;
import model.UserData;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class GameAccessSQL implements GameAccess{

    public GameAccessSQL() throws DataAccessException {
        buildGameTable();
    }

    public void clear() throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DROP TABLE gameData";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to drop table: %s", ex.getMessage()));
        }
        buildGameTable();
    }

    public Integer addGame(GameData newGame) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO gameData (whiteUsername, blackUsername, gameName) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
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
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to add gameData: %s", ex.getMessage()));
        }
    }

    public GameData getGame(Integer gameID) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT whiteUsername, blackUsername, gameName FROM gameData WHERE id=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
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
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to fetch gameData: %s", ex.getMessage()));
        }
    }

    public Collection<GameData> listGames() throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT id, whiteUsername, blackUsername, gameName FROM gameData";
            try (var preparedStatement = conn.prepareStatement(statement)) {
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
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to fetch gameData: %s", ex.getMessage()));
        }
    }

    public void updateGame(GameData game) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE gameData SET whiteUsername=?, blackUsername=? WHERE id=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setInt(3, game.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to fetch gameData: %s", ex.getMessage()));
        }
    }

    private void buildGameTable() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = """
                CREATE TABLE IF NOT EXISTS gameData (
                 id INT NOT NULL AUTO_INCREMENT,
                 whiteUsername varchar(256),
                 blackUsername varchar(256),
                 gameName varchar(256),
                PRIMARY KEY (id)  )
            """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure table userData: %s", ex.getMessage()));
        }
    }
}
