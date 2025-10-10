package dataaccess;

import model.AuthData;

import java.util.Map;
import java.util.HashMap;

public class AuthDataAccess {
    private static final Map<String, AuthData> AuthInfo = new HashMap<>();
    private static final Map<String, String> AssignedTokens = new HashMap<>();

    public static void clear() throws DataAccessException{
        AuthInfo.clear();
        AssignedTokens.clear();
    }

    public static void addAuthData(AuthData authData) throws DataAccessException {
        AuthInfo.put(authData.authToken(), authData);
        AssignedTokens.put(authData.username(), authData.authToken());
    }

    public static AuthData getAuthData(String authToken) throws DataAccessException{
        return AuthInfo.get(authToken);
    }

    public static String getAuthToken(String authToken) throws DataAccessException{
        return AssignedTokens.get(authToken);
    }

    public static void deleteAuth(AuthData authData) throws DataAccessException{
        AuthInfo.remove(authData.authToken());
        AssignedTokens.remove(authData.username());
    }
}
