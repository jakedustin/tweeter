package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterUserResponse;

public interface IRegisteredUserDAO {
    RegisterUserResponse registerUser(RegisterUserRequest request) throws Exception;
    String getHashedPassword(LoginRequest request) throws Exception;
}
