package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;

public class GetFeedRequest {
    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * The user whose feed is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private String userAlias;
    /**
     * Maximum number of statuses to return (i.e., page size).
     */
    private int limit;
    /**
     * The last status returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private StatusDTO lastStatus;

    public GetFeedRequest() {}

    public GetFeedRequest(AuthToken authToken, String userAlias, int limit, StatusDTO lastStatus) {
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

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public StatusDTO getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(StatusDTO lastStatus) {
        this.lastStatus = lastStatus;
    }
}