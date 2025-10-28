package dataaccess;

import model.AuthData;
import java.util.Map;
import java.util.HashMap;

public class AuthDataAccess implements  AuthAccess{
    private final Map<String, AuthData> authInfo = new HashMap<>();
    private final Map<String, String> assignedTokens = new HashMap<>();

    public AuthDataAccess() {

    }

    public void clear() throws DataAccessException{
        authInfo.clear();
        assignedTokens.clear();
    }

    public void addAuthData(AuthData authData) throws DataAccessException {
        authInfo.put(authData.authToken(), authData);
        assignedTokens.put(authData.username(), authData.authToken());
    }

    public AuthData getAuthData(String authToken) throws DataAccessException{
        return authInfo.get(authToken);
    }

    public String getAuthToken(String authToken) throws DataAccessException{
        return assignedTokens.get(authToken);
    }

    public void deleteAuth(AuthData authData) throws DataAccessException{
        authInfo.remove(authData.authToken());
        assignedTokens.remove(authData.username());
    }
}
