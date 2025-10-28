package dataaccess;

import model.UserData;

import java.util.Map;
import java.util.HashMap;

public class UserDataAccess implements UserAccess{
    private final Map<String, UserData> userInfo;

    public UserDataAccess() {
        userInfo = new HashMap<>();
    }

    public void clear() throws DataAccessException {
        userInfo.clear();
    }

    public void addUser(UserData userData) throws DataAccessException {
        userInfo.put(userData.username(), userData);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userInfo.get(username);
    }
}
