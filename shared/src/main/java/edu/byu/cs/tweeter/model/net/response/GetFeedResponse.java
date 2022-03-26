package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.Status;

import java.util.List;

public class GetFeedResponse extends PagedResponse {
    private List<Status> statusList;

    public GetFeedResponse() {
        super(false, false);
    }

    public GetFeedResponse(boolean success, boolean hasMorePages) {
        super(success, hasMorePages);
    }

    public GetFeedResponse(boolean success, String message, boolean hasMorePages) {
        super(success, message, hasMorePages);
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }
}
