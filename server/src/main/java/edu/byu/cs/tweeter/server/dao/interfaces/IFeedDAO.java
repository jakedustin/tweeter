package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.server.util.Pair;

public interface IFeedDAO {
    Pair<List<StatusDTO>, Boolean> getFeed(String userAlias, int limit, StatusDTO lastStatus) throws Exception;
    void postStatusToFeed(StatusDTO status, String userAlias) throws Exception;
}
