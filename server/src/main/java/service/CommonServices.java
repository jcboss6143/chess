package service;

import dataaccess.*;
import model.AuthData;

public class CommonServices {
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;
    private final UserAccess userAccess;

    public CommonServices(AuthAccess authAccess1, GameAccess gameAccess1, UserAccess userAccess1){
        this.authAccess = authAccess1;
        this.gameAccess = gameAccess1;
        this.userAccess = userAccess1;
    }

    public void deleteAllData() throws DataAccessException{
        authAccess.clear();
        gameAccess.clear();
        userAccess.clear();
    }

    public void deleteGameData() throws DataAccessException{
        gameAccess.clear();
    }

    public AuthData getAndVerifyAuthData(String authToken) throws ServiceException, DataAccessException {
        if (authToken == null) { throw new ServiceException("400"); }
        AuthData authInfo = authAccess.getAuthData(authToken);
        if (authInfo == null) { throw new ServiceException("401"); } // not a valid auth token
        return authInfo;
    }
}
