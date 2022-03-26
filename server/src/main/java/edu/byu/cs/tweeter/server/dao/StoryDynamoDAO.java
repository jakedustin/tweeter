package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IStoryDAO;

public class StoryDynamoDAO implements IStoryDAO {
    @Override
    public GetStoryResponse getStory(GetStoryRequest request) throws Exception {
        return null;
    }

    @Override
    public void postStatusToStory(String userAlias) throws Exception {

    }
}
