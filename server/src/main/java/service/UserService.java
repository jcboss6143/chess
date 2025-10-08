package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import service.model.LoginRequest;

public class UserService {
    public static AuthData register(UserData registerRequest) throws ServiceException {
        try {
            //verifying username is available and adding user
            UserData existingUser = UserDataAccess.getUser(registerRequest.username());
            if (existingUser != null){ throw new ServiceException("Username already taken"); }
            UserDataAccess.addUser(registerRequest);
            //create and add AuthData for user
            AuthData authData = createAuthData(registerRequest);
            AuthDataAccess.addAuth(authData);
            return authData;

        } catch (DataAccessException e) {
            throw new ServiceException("implement");
        }
    }


    public static AuthData login(LoginRequest loginRequest) throws ServiceException {
        throw new ServiceException("implement");
    }
    public static void logout(String authToken) throws ServiceException {
        throw new ServiceException("implement");
    }



    private static AuthData createAuthData(UserData userData) throws ServiceException {
        throw new ServiceException("implement");
    }
}
