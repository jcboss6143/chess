package dataaccess;

import model.UserData;

import java.util.Map;
import java.util.HashMap;

public class UserDataAccess {
    private static final Map<String, UserData> userInfo = new HashMap<>();

    public static void clear() throws DataAccessException {
        userInfo.clear();
    }

    public static void addUser(UserData userData) throws DataAccessException {
        userInfo.put(userData.username(), userData);
    }

    public static UserData getUser(String username) throws DataAccessException {
        return userInfo.get(username);
    }
}
