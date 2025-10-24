package dataaccess;

import model.AuthData;
import java.util.Map;
import java.util.HashMap;

public class AuthDataAccess implements  AuthAccess{
    private final Map<String, AuthData> AUTH_INFO = new HashMap<>();
    private final Map<String, String> ASSIGNED_TOKENS = new HashMap<>();

    public AuthDataAccess() {

    }

    public void clear() throws DataAccessException{
        AUTH_INFO.clear();
        ASSIGNED_TOKENS.clear();
    }

    public void addAuthData(AuthData authData) throws DataAccessException {
        AUTH_INFO.put(authData.authToken(), authData);
        ASSIGNED_TOKENS.put(authData.username(), authData.authToken());
    }

    public AuthData getAuthData(String authToken) throws DataAccessException{
        return AUTH_INFO.get(authToken);
    }

    public String getAuthToken(String authToken) throws DataAccessException{
        return ASSIGNED_TOKENS.get(authToken);
    }

    public void deleteAuth(AuthData authData) throws DataAccessException{
        AUTH_INFO.remove(authData.authToken());
        ASSIGNED_TOKENS.remove(authData.username());
    }
}
