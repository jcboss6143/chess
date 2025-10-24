package dataaccess;

import model.AuthData;

public interface AuthAccess {
    void clear() throws DataAccessException;
    void addAuthData(AuthData authData) throws DataAccessException;
    AuthData getAuthData(String authToken) throws DataAccessException;
    String getAuthToken(String authToken) throws DataAccessException;
    void deleteAuth(AuthData authData) throws DataAccessException;

}
