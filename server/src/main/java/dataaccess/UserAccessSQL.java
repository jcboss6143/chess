package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UserAccessSQL implements  UserAccess, CommonAccessSQL{
    public UserAccessSQL() throws DataAccessException{
        buildUserTable();
    }

    public void clear() throws DataAccessException {
        String statement = "DROP TABLE userData";
        String errorMessage = "Unable to drop table";
        sendStatement(statement, errorMessage, this::executeUpdateCore, 1);
        buildUserTable();
    }



    public void addUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO userData (username, password, email) VALUES (?, ?,?)";
        String errorMessage = "Unable to add userData";
        sendStatement(statement, errorMessage, this::addUserCore, userData);
    }

    public int addUserCore(PreparedStatement preparedStatement, UserData userData) throws SQLException {
        preparedStatement.setString(1,userData.username());
        preparedStatement.setString(2,userData.password());
        preparedStatement.setString(3,userData.email());
        preparedStatement.executeUpdate();
        return 1;
    }




    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT username, password, email FROM userData WHERE username=?";
        String errorMessage = "Unable to fetch userData";
        return sendStatement(statement, errorMessage, this::getUserCore, username);
    }


    public UserData getUserCore(PreparedStatement preparedStatement, String username) throws SQLException {
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





    private void buildUserTable() throws DataAccessException{
        DatabaseManager.createDatabase();
        String statement = """
                CREATE TABLE IF NOT EXISTS userData (
                 username varchar(256) NOT NULL,
                 password varchar(256) NOT NULL,
                 email varchar(256) NOT NULL,
                PRIMARY KEY (username)  )
            """;
        String errorMessage = "Unable to configure table userData";
        sendStatement(statement, errorMessage, this::executeUpdateCore, 1);
    }
}
