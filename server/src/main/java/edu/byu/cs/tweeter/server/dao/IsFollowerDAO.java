package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

public class IsFollowerDAO {
    public IsFollowerDAO() {}

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        //TODO: implement business logic to check relationship between two users
        IsFollowerResponse response = new IsFollowerResponse( true, "This message came from the API");
        response.setIsFollower(true);
        return response;
    }
}
