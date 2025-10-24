package dataaccess;

import model.AuthData;
import java.util.Map;
import java.util.HashMap;

public class AuthDataAccess {
    private static final Map<String, AuthData> AUTH_INFO = new HashMap<>();
    private static final Map<String, String> ASSIGNED_TOKENS = new HashMap<>();

    public static void clear() throws DataAccessException{
        AUTH_INFO.clear();
        ASSIGNED_TOKENS.clear();
    }

    public static void addAuthData(AuthData authData) throws DataAccessException {
        AUTH_INFO.put(authData.authToken(), authData);
        ASSIGNED_TOKENS.put(authData.username(), authData.authToken());
    }

    public static AuthData getAuthData(String authToken) throws DataAccessException{
        return AUTH_INFO.get(authToken);
    }

    public static String getAuthToken(String authToken) throws DataAccessException{
        return ASSIGNED_TOKENS.get(authToken);
    }

    public static void deleteAuth(AuthData authData) throws DataAccessException{
        AUTH_INFO.remove(authData.authToken());
        ASSIGNED_TOKENS.remove(authData.username());
    }
}
