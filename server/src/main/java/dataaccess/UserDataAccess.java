package dataaccess;

import model.UserData;

import java.util.Map;
import java.util.HashMap;

public class UserDataAccess implements UserAccess{
    private final Map<String, UserData> USER_INFO;

    public UserDataAccess() {
        USER_INFO = new HashMap<>();
    }

    public void clear() throws DataAccessException {
        USER_INFO.clear();
    }

    public void addUser(UserData userData) throws DataAccessException {
        USER_INFO.put(userData.username(), userData);
    }

    public UserData getUser(String username) throws DataAccessException {
        return USER_INFO.get(username);
    }
}
