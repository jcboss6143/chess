package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;

public class DatabaseService {
    public DatabaseService() {
    }

    public static void deleteAllData() throws ServiceException{
        try {
            AuthDataAccess.clear();
            GameDataAccess.clear();
            UserDataAccess.clear();
        }
        catch (DataAccessException e) {
            throw new ServiceException("implement");
        }
        throw new ServiceException("implement");
    }
}
