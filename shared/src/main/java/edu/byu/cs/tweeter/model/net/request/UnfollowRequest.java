package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowRequest {
    private AuthToken authToken;
    private String followerAlias;
    private String followeeAlias;

    public UnfollowRequest() {}

    public UnfollowRequest(AuthToken authToken, String followerAlias, String followeeAlias) {
        this.authToken = authToken;
        this.followerAlias = followeeAlias;
        this.followerAlias = followerAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFollowerAlias() {
        return followerAlias;
    }

    public void setFollowerAlias(String followerAlias) {
        this.followerAlias = followerAlias;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }
}
