package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import service.model.LoginRequest;

import java.security.SecureRandom;
import java.util.Arrays;

public class UserService {
    public static AuthData register(UserData registerRequest) throws ServiceException, DataAccessException {
        //verifying username is available and adding user
        UserData existingUser = UserDataAccess.getUser(registerRequest.username());
        if (existingUser != null){ throw new ServiceException("403"); }
        UserDataAccess.addUser(registerRequest);
        //create and add AuthData for user
        String authToken = createAuthToken();
        AuthData authData = new AuthData(authToken, registerRequest.username());
        AuthDataAccess.addAuth(authData);
        return authData;
    }

    public static AuthData login(LoginRequest loginRequest) throws ServiceException, DataAccessException {
        throw new ServiceException("implement");
    }

    public static void logout(String authToken) throws ServiceException, DataAccessException {
        throw new ServiceException("implement");
    }

    private static String createAuthToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Arrays.toString(bytes);
    }
}
