package dataaccess;

import model.AuthData;

import java.util.Map;
import java.util.HashMap;

public class AuthDataAccess {
    private static final Map<String, AuthData> authInfo = new HashMap<>();

    public static void clear() throws DataAccessException{
        authInfo.clear();
    }

    public static void addAuth(AuthData authData) throws DataAccessException{
        authInfo.put(authData.authToken(), authData);
    }

    public static AuthData getAuth(String authToken) throws DataAccessException{
        return authInfo.get(authToken);
    }

    public static void deleteAuth(AuthData authData) throws DataAccessException{
        authInfo.remove(authData.authToken());
    }
}
