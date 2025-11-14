package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import model.LoginRequest;
import java.security.SecureRandom;
import java.util.Base64;

public class UserService {
    private final AuthAccess authAccess;
    private final UserAccess userAccess;
    private final CommonServices commonServices;

    public UserService(AuthAccess authAccess, UserAccess userAccess, CommonServices commonServices) {
        this.authAccess = authAccess;
        this.userAccess = userAccess;
        this.commonServices = commonServices;
    }

    public AuthData register(UserData registerRequest) throws ServiceException, DataAccessException {
        if ((registerRequest.password() == null) || (registerRequest.username() == null) || (registerRequest.email() == null)){
            throw new ServiceException("400"); }
        UserData existingUser = userAccess.getUser(registerRequest.username());
        if (existingUser != null){ throw new ServiceException("403"); } // verifying username is available
        userAccess.addUser(registerRequest);
        return createAuthObject(registerRequest.username());
    }

    public AuthData login(LoginRequest loginRequest) throws ServiceException, DataAccessException {
        if ((loginRequest.password() == null) || (loginRequest.username() == null)){
            throw new ServiceException("400"); }
        UserData userProfile = userAccess.getUser(loginRequest.username());
        if ((userProfile == null) || (!BCrypt.checkpw(loginRequest.password(), userProfile.password()))) {
            throw new ServiceException("401"); } // verifies user exists and passwords match
        return createAuthObject(loginRequest.username());
    }

    public void logout(String authToken) throws ServiceException, DataAccessException {
        authAccess.deleteAuth(commonServices.getAndVerifyAuthData(authToken));
    }


    public String getUsernameFromAuth(String authToken) throws DataAccessException, DataAccessException {
        AuthData userInfo = authAccess.getAuthData(authToken);
        return userInfo.username();
    }


    private AuthData createAuthObject(String username) throws DataAccessException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        String authToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        AuthData authData = new AuthData(authToken, username);
        authAccess.addAuthData(authData);
        return authData;
    }
}
