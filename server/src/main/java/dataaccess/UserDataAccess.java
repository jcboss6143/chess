package dataaccess;

import model.UserData;

import java.util.Map;
import java.util.HashMap;

public class UserDataAccess {
    private static final Map<String, UserData> USER_INFO = new HashMap<>();

    public static void clear() throws DataAccessException {
        USER_INFO.clear();
    }

    public static void addUser(UserData userData) throws DataAccessException {
        USER_INFO.put(userData.username(), userData);
    }

    public static UserData getUser(String username) throws DataAccessException {
        return USER_INFO.get(username);
    }
}
