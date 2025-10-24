package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;


public class UserAccessSQL implements  UserAccess{
    public UserAccessSQL() throws DataAccessException{
        buildUserTable();
    }

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DROP TABLE userData";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to drop table: %s", ex.getMessage()));
        }
        buildUserTable();
    }

    public void addUser(UserData userData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO userData (username, password, email) VALUES (?, ?,?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,userData.username());
                preparedStatement.setString(2,userData.password());
                preparedStatement.setString(3,userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to add userData: %s", ex.getMessage()));
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM userData WHERE username=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to fetch userData: %s", ex.getMessage()));
        }
    }

    private void buildUserTable() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = """
                CREATE TABLE IF NOT EXISTS userData (
                 username varchar(256) NOT NULL,
                 password varchar(256) NOT NULL,
                 email varchar(256) NOT NULL,
                PRIMARY KEY (username)  )
            """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure table userData: %s", ex.getMessage()));
        }
    }
}
