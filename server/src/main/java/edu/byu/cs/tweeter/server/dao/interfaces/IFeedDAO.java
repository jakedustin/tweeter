package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;

public interface IFeedDAO {
    List<StatusDTO> getFeed(String userAlias, int limit, StatusDTO lastStatus) throws Exception;
    void postStatusToFeed(StatusDTO status, String userAlias) throws Exception;
}
