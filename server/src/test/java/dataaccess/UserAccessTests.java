package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;

public class UserAccessTests implements CommonAccessSQL {

    private final UserAccess userAccess = new UserAccessSQL();


    public UserAccessTests() throws DataAccessException {
    }

    @Test
    @DisplayName("successfully clears board")
    public void clearSuccess() throws DataAccessException {
        userAccess.clear();
        String statement = "SELECT * from userData";
        String errorMessage = "Failed";
        boolean tableCleared = sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            try (var rs = preparedStatement.executeQuery()) {
                return !rs.next();
            }
        });
        Assertions.assertTrue(tableCleared);
    }


    @Test
    @DisplayName("create new user")
    public void createUserSuccess() throws DataAccessException {
        userAccess.clear();
        userAccess.addUser(new UserData("joe", "password", "joe@email.com"));
        String statement = "SELECT * from userData WHERE username=?";
        String errorMessage = "Failed";
        boolean correctOutput = sendStatement(statement, errorMessage, (PreparedStatement preparedStatement) -> {
            preparedStatement.setString(1, "joe");
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Assertions.assertEquals("joe@email.com", rs.getString("email"));
                    Assertions.assertTrue(BCrypt.checkpw("password", rs.getString("password")));
                    Assertions.assertEquals("joe", rs.getString("username"));
                    return true;
                }
                return false;
            }
        });
        Assertions.assertTrue(correctOutput);
    }


    @Test
    @DisplayName("create new user with bad data")
    public void createUserFail() throws DataAccessException {
        userAccess.clear();
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            userAccess.addUser(new UserData(null, null, null));
        });
        Assertions.assertEquals("Unable to add userData: Column 'username' cannot be null", exception.getMessage());
    }


    @Test
    @DisplayName("get user")
    public void getUserSuccess() throws DataAccessException {
        userAccess.clear();
        userAccess.addUser(new UserData("joe", "password", "joe@email.com"));
        UserData userData = userAccess.getUser("joe");
        Assertions.assertEquals("joe@email.com", userData.email());
        Assertions.assertTrue(BCrypt.checkpw("password", userData.password()));
        Assertions.assertEquals("joe", userData.username());
    }


    @Test
    @DisplayName("get nonexistent user")
    public void getUserFail() throws DataAccessException {
        userAccess.clear();
        userAccess.addUser(new UserData("joe", "password", "joe@email.com"));
        userAccess.clear();
        Assertions.assertNull(userAccess.getUser("joey"));
    }
}
