package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import java.util.List;

public class GetFeedDAO {

    public GetFeedDAO() {}

    public GetFeedResponse getFeed(GetFeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getTargetUser() != null;

        Pair<List<Status>, Boolean> feed = getDummyFeed(request);

        GetFeedResponse response = new GetFeedResponse(true, feed.getSecond());
        response.setStatusList(feed.getFirst());

        return response;
    }

    private Pair<List<Status>, Boolean> getDummyFeed(GetFeedRequest request) {
        return getFakeData().getPageOfStatus(request.getLastStatus(), request.getLimit());
    }

    private FakeData getFakeData() {
        return new FakeData();
    }
}
