package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetFollowersCountRequest {

    private AuthToken authToken;
    private String followeeAlias;


    private GetFollowersCountRequest() {}

    public GetFollowersCountRequest(AuthToken authToken, String followeeAlias) {
        this.authToken = authToken;
        this.followeeAlias = followeeAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followerAlias) {
        this.followeeAlias = followerAlias;
    }
}
