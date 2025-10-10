package dataaccess;

import model.UserData;

import java.util.Map;
import java.util.HashMap;

public class UserDataAccess {
    private static final Map<String, UserData> UserInfo = new HashMap<>();

    public static void clear() throws DataAccessException {
        UserInfo.clear();
    }

    public static void addUser(UserData userData) throws DataAccessException {
        UserInfo.put(userData.username(), userData);
    }

    public static UserData getUser(String username) throws DataAccessException {
        return UserInfo.get(username);
    }
}
