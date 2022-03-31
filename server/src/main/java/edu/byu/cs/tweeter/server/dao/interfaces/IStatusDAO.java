package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

public interface IStatusDAO {
    PostStatusResponse postStatus(String post, String userAlias, String datetime, List<String> urls, List<String> mentions) throws Exception;

}
