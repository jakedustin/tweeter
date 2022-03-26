package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.util.FakeData;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;

public class GetUserDAO {
    public GetUserDAO() {}

    public GetUserResponse getUser(GetUserRequest request) {
        GetUserResponse response = new GetUserResponse(getUserData(request.getAlias()));
        return response;
    }

    private User getUserData(String alias) {
        return getFakeData().findUserByAlias(alias);
    }

    private FakeData getFakeData() {
        return new FakeData();
    }
}
