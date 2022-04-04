package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.server.util.Pair;

public interface IStoryDAO {
    Pair<List<StatusDTO>, Boolean> getStory(String userAlias, int limit, StatusDTO lastStatus) throws Exception;
    void postStatusToStory(StatusDTO status) throws Exception;

}
