package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.LoginRequest;

public class UserServiceTests {

    private final UserData userObject = new UserData("Joe", "J03s-p4ssw0rd", "Joe.Email@google.com");


    public void validateAuthData(AuthData authData) {
        Assertions.assertEquals(authData.username(), userObject.username());
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals(32, authData.authToken().length());
    }

    @Test
    @DisplayName("Register New User")
    public void registerUserSuccess() throws DataAccessException {
        DatabaseService.deleteAllData();
        Assertions.assertDoesNotThrow(() -> {
            AuthData authObject = UserService.register(userObject);
            validateAuthData(authObject);
        });
    }

    @Test
    @DisplayName("Username Already Taken")
    public void registerUserAlreadyExists() throws DataAccessException {
        registerUserSuccess();
        UserData userObject2 = new UserData("Joe", "J03s-p4ssw0rd2", "Joe.Email2@google.com");
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            AuthData authObject = UserService.register(userObject2);
        });
        Assertions.assertEquals("403", exception.getMessage());
    }


    @Test
    @DisplayName("loging in when user still has active token")
    public void logInWithActiveToken() throws DataAccessException {
        registerUserSuccess();
        Assertions.assertDoesNotThrow(() -> {
            AuthData authObject = UserService.login(new LoginRequest(userObject.username(), userObject.password()));
            validateAuthData(authObject);
        });
    }

}
