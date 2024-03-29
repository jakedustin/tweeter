package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;

public interface IStoryDAO {
    GetStoryResponse getStory(GetStoryRequest request) throws Exception;
    void postStatusToStory(String userAlias) throws Exception;
}
