package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;

public class GetStoryRequest {
    private AuthToken authToken;
    private String userAlias;
    private int limit;
    private StatusDTO lastStatus;

    public GetStoryRequest() {}

    public GetStoryRequest(AuthToken authToken, String userAlias, int limit, StatusDTO lastStatus) {
        this.authToken = authToken;
        this.userAlias = userAlias;
        this.limit = limit;
        this.lastStatus = lastStatus;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public StatusDTO getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(StatusDTO lastStatus) {
        this.lastStatus = lastStatus;
    }
}
