package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.dto.UserDTO;
import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;

public interface IUserDAO {
    User getUser(String userAlias) throws Exception;
    User postNewUser(RegisterUserRequest request) throws Exception;
    void addUserBatch(List<UserDTO> users);
}
