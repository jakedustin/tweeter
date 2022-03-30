package edu.byu.cs.tweeter.server.dao.interfaces;

import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.dto.UserDTO;
import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;

public interface IUserDAO {
    User getUser(String userAlias) throws Exception;
    User postNewUser(RegisterUserRequest request) throws Exception;
    // for testing only
    PutItemOutcome postNewUser(String alias, String firstName, String lastName, String imageUrl) throws Exception;
    void adjustFollowingValue(boolean isIncrement, String alias);
    void adjustFollowersValue(boolean isIncrement, String alias);
    void addUserBatch(List<UserDTO> users);
    void deleteUsers(List<String> userHandles);
}
