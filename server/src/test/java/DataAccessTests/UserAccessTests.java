package DataAccessTests;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.CreateGameRequest;
import service.model.JoinGameRequest;
import service.model.ListGamesResult;

import java.sql.PreparedStatement;

public class UserAccessTests implements CommonAccessSQL {

    private final UserAccess userAccess = new UserAccessSQL();


    public UserAccessTests() throws DataAccessException {

    }

    @Test
    @DisplayName("create new game")
    public void clearSuccess() throws DataAccessException {
        userAccess.clear();
        String statement = "SELECT * from gameData";
        String errorMessage = "Failed";
        boolean tableCleared = sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            try (var rs = preparedStatement.executeQuery()) {
                return !rs.next();
            }
        });
        Assertions.assertTrue(tableCleared);
    }
}
