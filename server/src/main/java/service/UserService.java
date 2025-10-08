package service;

import model.UserData;
import service.model.LoginRequest;
import service.model.LoginResult;

public class UserService {
    public LoginResult register(UserData registerRequest) throws ServiceException {
        throw new ServiceException("implement");
    }
    public LoginResult login(LoginRequest loginRequest) throws ServiceException {
        throw new ServiceException("implement");
    }
    public void logout(String authToken) throws ServiceException {
        throw new ServiceException("implement");
    }
}
