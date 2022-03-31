package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;

public class PostStatusRequest {
    private AuthToken authToken;
    private StatusDTO status;

    public PostStatusRequest() {}

    public PostStatusRequest(AuthToken authToken, StatusDTO status) {
        this.authToken = authToken;
        this.status = status;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }
}
