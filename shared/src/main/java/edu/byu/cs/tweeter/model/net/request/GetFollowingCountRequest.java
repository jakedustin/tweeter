package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetFollowingCountRequest {

    private AuthToken authToken;
    private String followerAlias;


    private GetFollowingCountRequest() {}

    public GetFollowingCountRequest(AuthToken authToken, String followerAlias) {
        this.authToken = authToken;
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
}
