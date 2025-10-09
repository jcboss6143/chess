package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;

public class DatabaseService {
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
}
