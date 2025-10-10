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
        if ((registerRequest.password() == null) || (registerRequest.username() == null) || (registerRequest.email() == null)){
            throw new ServiceException("400"); }
        UserData existingUser = UserDataAccess.getUser(registerRequest.username());
        if (existingUser != null){ throw new ServiceException("403"); } // verifying username is available
        UserDataAccess.addUser(registerRequest);
        return createAuthObject(registerRequest.username());
    }

    public static AuthData login(LoginRequest loginRequest) throws ServiceException, DataAccessException {
        if ((loginRequest.password() == null) || (loginRequest.username() == null)){
            throw new ServiceException("400"); }
        UserData userProfile = UserDataAccess.getUser(loginRequest.username());
        if ((userProfile == null) || (!Objects.equals(userProfile.password(), loginRequest.password()))) {
            throw new ServiceException("401"); } // verifies user exists and passwords match
        return createAuthObject(loginRequest.username());
    }

    public static void logout(String authToken) throws ServiceException, DataAccessException {
        AuthDataAccess.deleteAuth(CommonServices.getAndVerifyAuthData(authToken));
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
