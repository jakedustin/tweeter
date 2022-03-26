package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;

public interface IFeedDAO {
    GetFeedResponse getFeed(GetFeedRequest request) throws Exception;
}
