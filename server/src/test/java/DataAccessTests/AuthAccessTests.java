package DataAccessTests;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.ServiceException;
import service.model.CreateGameRequest;
import service.model.JoinGameRequest;
import service.model.ListGamesResult;

import java.sql.PreparedStatement;

public class AuthAccessTests implements CommonAccessSQL {

    private final AuthAccess authAccess = new AuthAccessSQL();;

    public AuthAccessTests() throws DataAccessException {

    }

    @Test
    @DisplayName("clears correct table")
    public void clearSuccess() throws DataAccessException {
        authAccess.clear();
        String statement = "SELECT * from authData";
        String errorMessage = "Failed";
        boolean tableCleared = sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            try (var rs = preparedStatement.executeQuery()) {
                return !rs.next();
            }
        });
        Assertions.assertTrue(tableCleared);
    }

    @Test
    @DisplayName("create new user and fetch it")
    public void AddUserSuccess() throws DataAccessException {
        authAccess.clear();
        authAccess.addAuthData(new AuthData("TestToken", "Joe"));
        String statement = "SELECT * from authData WHERE authToken='TestToken'";
        String errorMessage = "Failed";
        boolean correctOutput = sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Assertions.assertEquals("TestToken", rs.getString("authToken"));
                    Assertions.assertEquals("Joe", rs.getString("username"));
                    return true;
                }
                return false;
            }
        });
        Assertions.assertTrue(correctOutput);
    }

    @Test
    @DisplayName("fetch bad user")
    public void GetUserFail() throws DataAccessException {
        authAccess.clear();
        Assertions.assertNull(authAccess.getAuthData("BadAuthToken"));
    }


    @Test
    @DisplayName("gets valid auth data")
    public void GetAuthDataSuccess() throws DataAccessException {
        authAccess.clear();
        authAccess.addAuthData(new AuthData("TestToken", "Joe"));
        AuthData returnedResult = authAccess.getAuthData("TestToken");
        Assertions.assertEquals("Joe", returnedResult.username());
        Assertions.assertEquals("TestToken", returnedResult.authToken());
    }


    @Test
    @DisplayName("fails to get auth data")
    public void GetAuthDataFail() throws DataAccessException {
        authAccess.clear();
        authAccess.addAuthData(new AuthData("TestToken", "Joe"));
        Assertions.assertNull(authAccess.getAuthData("BadTestToken"));
    }


    @Test
    @DisplayName("fails to get auth data")
    public void DeleteAuthSuccess() throws DataAccessException {
        authAccess.clear();
        authAccess.addAuthData(new AuthData("TestToken", "Joe"));
        authAccess.deleteAuth(new AuthData("TestToken", "Joe"));
        Assertions.assertNull(authAccess.getAuthData("TestToken"));
    }

    @Test
    @DisplayName("fails to get auth data")
    public void DeleteAuthFail() throws DataAccessException {
        authAccess.clear();
        authAccess.addAuthData(new AuthData("TestToken", "Joe"));
        authAccess.deleteAuth(new AuthData("BadTestToken", "Joe"));
        Assertions.assertNotNull(authAccess.getAuthData("TestToken"));
    }

}
