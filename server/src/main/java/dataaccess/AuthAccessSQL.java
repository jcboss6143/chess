package dataaccess;

import model.*;

import java.sql.*;

import model.AuthData;
import service.ServiceException;


public class AuthAccessSQL implements AuthAccess, CommonAccessSQL{

    public AuthAccessSQL() throws DataAccessException {
        buildAuthTable();
    }


    public void clear() throws DataAccessException{
        String statement = "DROP TABLE authData";
        String errorMessage = "Unable to drop table";
        sendStatement(statement, errorMessage, this::executeUpdateCore, 1);
        buildAuthTable();
    }



    public void addAuthData(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO authData (username, authToken) VALUES (?, ?)";
        String errorMessage = "Unable to add authData";
        sendStatement(statement, errorMessage, this::addAuthDataCore, authData);
    }

    public int addAuthDataCore(PreparedStatement preparedStatement, AuthData authData)  throws SQLException  {
        preparedStatement.setString(1,authData.username());
        preparedStatement.setString(2,authData.authToken());
        preparedStatement.executeUpdate();
        return 1;
    }



    public AuthData getAuthData(String authToken) throws DataAccessException{
        String statement = "SELECT username, authToken FROM authData WHERE authToken=?";
        String errorMessage = "Unable to fetch authData";
        return sendStatement(statement, errorMessage, this::getAuthDataCore, authToken);
    }

    private AuthData getAuthDataCore(PreparedStatement preparedStatement, String authToken) throws SQLException {
        preparedStatement.setString(1, authToken);
        try (var rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                String username = rs.getString("username");
                return new AuthData(authToken, username);
            }
            return null;
        }
    }




    public String getAuthToken(String username) throws DataAccessException{
        String statement = "SELECT username, authToken FROM authData WHERE username=?";
        String errorMessage = "Unable to get token";
        return sendStatement(statement, errorMessage, this::getAuthTokenCore, username);
    }

    public String getAuthTokenCore(PreparedStatement preparedStatement, String username) throws SQLException {
        preparedStatement.setString(1, username);
        try (var rs = preparedStatement.executeQuery()) {
            if (rs.next()) { return rs.getString("authToken"); }
            return null;
        }
    }



    public void deleteAuth(AuthData authData) throws DataAccessException{
        String statement = "DELETE FROM authData WHERE authToken=?";
        String errorMessage = "Unable to delete authData";
        sendStatement(statement, errorMessage, this::deleteAuthCore, authData);
    }

    public int deleteAuthCore(PreparedStatement preparedStatement, AuthData authData) throws SQLException {
        preparedStatement.setString(1, authData.authToken());
        preparedStatement.executeUpdate();
        return 1;
    }



    private void buildAuthTable() throws DataAccessException{
        DatabaseManager.createDatabase();
        String statement = """
                CREATE TABLE IF NOT EXISTS  authData (
                 username varchar(256) NOT NULL,
                 authToken varchar(256) NOT NULL,
                PRIMARY KEY (authToken)  )
            """;
        String errorMessage = "Unable to configure table authData";
        sendStatement(statement, errorMessage, this::executeUpdateCore, 1);
    }

}
