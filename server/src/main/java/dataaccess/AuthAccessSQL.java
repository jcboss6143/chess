package dataaccess;

import model.*;

import java.sql.*;

import model.AuthData;


public class AuthAccessSQL implements AuthAccess{

    public AuthAccessSQL() throws DataAccessException {
        buildAuthTable();
    }


    public void clear() throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DROP TABLE authData";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to drop table: %s", ex.getMessage()));
        }
        buildAuthTable();
    }


    public void addAuthData(AuthData authData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO authData (username, authToken) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,authData.username());
                preparedStatement.setString(2,authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to add authData: %s", ex.getMessage()));
        }
    }


    public AuthData getAuthData(String authToken) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, authToken FROM authData WHERE authToken=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to fetch authData: %s", ex.getMessage()));
        }
    }


    public String getAuthToken(String username) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, authToken FROM authData WHERE username=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) { return rs.getString("authToken"); }
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to get token: %s", ex.getMessage()));
        }
    }


    public void deleteAuth(AuthData authData) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM authData WHERE authToken=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to delete authData: %s", ex.getMessage()));
        }
    }


    private void buildAuthTable() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = """
                CREATE TABLE IF NOT EXISTS  authData (
                 username varchar(256) NOT NULL,
                 authToken varchar(256) NOT NULL,
                PRIMARY KEY (authToken)  )
            """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure table authData: %s", ex.getMessage()));
        }
    }
}
