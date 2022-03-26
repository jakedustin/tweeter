package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

public interface IStatusDAO {
    PostStatusResponse postStatus(PostStatusRequest request) throws Exception;

}
