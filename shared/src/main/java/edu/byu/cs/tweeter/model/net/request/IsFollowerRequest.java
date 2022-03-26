package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerRequest {
    private AuthToken authToken;
    private User follower;
    private User followee;

    public IsFollowerRequest() {}

    public IsFollowerRequest(AuthToken authToken, User follower, User followee) {
        this.authToken = authToken;
        this.follower = follower;
        this.follower.setImageBytes(null);
        this.followee = followee;
        this.followee.setImageBytes(null);
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }

}
