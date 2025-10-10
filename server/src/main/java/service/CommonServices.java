package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import model.AuthData;

public class CommonServices {
    public static void deleteAllData() throws DataAccessException{
        AuthDataAccess.clear();
        GameDataAccess.clear();
        UserDataAccess.clear();
    }

    public static void deleteAuthData() throws DataAccessException{
        AuthDataAccess.clear();
    }

    public static void deleteGameData() throws DataAccessException{
        GameDataAccess.clear();
    }

    public static void deleteUserData() throws DataAccessException{
        UserDataAccess.clear();
    }

    public static AuthData getAndVerifyAuthData(String authToken) throws ServiceException, DataAccessException {
        if (authToken == null) { throw new ServiceException("400"); }
        AuthData authInfo = AuthDataAccess.getAuthData(authToken);
        if (authInfo == null) { throw new ServiceException("401"); } // not a valid auth token
        return authInfo;
    }
}
