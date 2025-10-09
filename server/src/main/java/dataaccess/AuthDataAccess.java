package dataaccess;

import model.AuthData;

import java.util.Map;
import java.util.HashMap;

public class AuthDataAccess {
    private static final Map<String, AuthData> authInfo = new HashMap<>();
    private static final Map<String, String> assignedTokens = new HashMap<>();

    public static void clear() throws DataAccessException{
        authInfo.clear();
        assignedTokens.clear();
    }

    public static void addAuthData(AuthData authData) throws DataAccessException {
        authInfo.put(authData.authToken(), authData);
        assignedTokens.put(authData.username(), authData.authToken());
    }

    public static AuthData getAuthData(String authToken) throws DataAccessException{
        return authInfo.get(authToken);
    }

    public static String getAuthToken(String authToken) throws DataAccessException{
        return assignedTokens.get(authToken);
    }

    public static void deleteAuth(AuthData authData) throws DataAccessException{
        authInfo.remove(authData.authToken());
        assignedTokens.remove(authData.username());
    }
}
