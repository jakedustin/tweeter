package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

public class UnfollowDAO {
    public UnfollowResponse unfollow(UnfollowRequest request) {
        return new UnfollowResponse(true);
    }
}
