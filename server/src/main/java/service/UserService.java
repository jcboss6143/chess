package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import service.model.LoginRequest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public class UserService {

    public static AuthData register(UserData registerRequest) throws ServiceException, DataAccessException {
        //verifying username is available and adding user
        UserData existingUser = UserDataAccess.getUser(registerRequest.username());
        if (existingUser != null){ throw new ServiceException("403"); }
        // adds user and returns authData for user
        UserDataAccess.addUser(registerRequest);
        return createAuthObject(registerRequest.username());
    }

    public static AuthData login(LoginRequest loginRequest) throws ServiceException, DataAccessException {
        // verifies user exists and passwords match
        UserData userProfile = UserDataAccess.getUser(loginRequest.username());
        if ((userProfile == null) || (!Objects.equals(userProfile.password(), loginRequest.password()))) {
            throw new ServiceException("401"); }
        // checks if user is already logged in, and deletes their current auth token if so
        String existingAuthToken = AuthDataAccess.getAuthToken(loginRequest.username());
        if (existingAuthToken != null) { AuthDataAccess.deleteAuth(AuthDataAccess.getAuthData(existingAuthToken)); }
        return createAuthObject(loginRequest.username());
    }

    public static void logout(String authToken) throws ServiceException, DataAccessException {
        AuthData authInfo = AuthDataAccess.getAuthData(authToken);
        if (authInfo == null) { throw new ServiceException("401"); } // not a valid auth token
        AuthDataAccess.deleteAuth(authInfo);
    }


    private static AuthData createAuthObject(String username) throws DataAccessException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        String authToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        AuthData authData = new AuthData(authToken, username);
        AuthDataAccess.addAuthData(authData);
        return authData;
    }
}
