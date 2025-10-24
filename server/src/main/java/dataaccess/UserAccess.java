package dataaccess;

import model.UserData;

public interface UserAccess {
    void clear() throws DataAccessException;
    void addUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

}
