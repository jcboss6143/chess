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
    private AuthData userObjectAuthData = null;

    public void validateAuthData(AuthData authData) {
        Assertions.assertEquals(authData.username(), userObject.username());
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals(32, authData.authToken().length());
    }

    public void registerAndLogOut() throws DataAccessException, ServiceException {
        DatabaseService.deleteAllData();
        userObjectAuthData = UserService.register(userObject);
        UserService.logout(userObjectAuthData.authToken());
    }



    @Test
    @DisplayName("Register New User")
    public void registerUserSuccess() throws DataAccessException {
        DatabaseService.deleteAllData();
        Assertions.assertDoesNotThrow(() -> {
            userObjectAuthData = UserService.register(userObject);
            validateAuthData(userObjectAuthData);
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
    @DisplayName("logging out when user has active token")
    public void logoutWithValidToken() throws DataAccessException {
        registerUserSuccess();
        Assertions.assertDoesNotThrow(() -> {
            UserService.logout(userObjectAuthData.authToken());
        });
    }


    @Test
    @DisplayName("logging out when user doesn't has active token")
    public void logoutWithoutValidToken() throws DataAccessException {
        DatabaseService.deleteAllData();
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            UserService.logout("Invalid_auth_token");
        });
        Assertions.assertEquals("401", exception.getMessage());
    }


    @Test
    @DisplayName("logging in when user doesn't have active token")
    public void logInWithoutActiveToken() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            registerAndLogOut();
            AuthData authObject = UserService.login(new LoginRequest(userObject.username(), userObject.password()));
            validateAuthData(authObject);
        });
    }



    @Test
    @DisplayName("logging in when user still has active token")
    public void logInWithActiveToken() throws DataAccessException {
        registerUserSuccess();
        Assertions.assertDoesNotThrow(() -> {
            AuthData authObject = UserService.login(new LoginRequest(userObject.username(), userObject.password()));
            validateAuthData(authObject);
        });
    }

    @Test
    @DisplayName("logging in with bad username")
    public void logInWithBadUsername() throws DataAccessException {
        registerUserSuccess();
        Assertions.assertDoesNotThrow(this::registerAndLogOut);
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            UserService.login(new LoginRequest("NotJoe", userObject.password()));
        });
        Assertions.assertEquals("401", exception.getMessage());
    }

    @Test
    @DisplayName("logging in with bad password")
    public void logInWithBadPassword() throws DataAccessException {
        registerUserSuccess();
        Assertions.assertDoesNotThrow(this::registerAndLogOut);
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            UserService.login(new LoginRequest(userObject.username(), "N0t-J03s-P4ssw0rd"));
        });
        Assertions.assertEquals("401", exception.getMessage());
    }

}
