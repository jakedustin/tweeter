package edu.byu.cs.tweeter.model.net.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;

public class PostStatusToFeedRequest {
    private List<String> followers;
    private StatusDTO status;

    public PostStatusToFeedRequest() {

    }

    public PostStatusToFeedRequest(List<String> followers, StatusDTO status) {
        this.followers = followers;
        this.status = status;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }
}
