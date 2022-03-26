package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import java.util.List;

public class GetStoryDAO {

    public GetStoryDAO() {}

    public GetStoryResponse getStory(GetStoryRequest request) {
        assert request.getLimit() > 0;
        assert request.getTargetUser() != null;

        Pair<List<Status>, Boolean> story = getDummyStory(request);

        GetStoryResponse response = new GetStoryResponse(story.getFirst(), story.getSecond());

        return response;
    }

    private Pair<List<Status>, Boolean> getDummyStory(GetStoryRequest request) {
        return getFakeData().getPageOfStatus(request.getLastStatus(), request.getLimit());
    }

    private FakeData getFakeData() {
        return new FakeData();
    }
}
