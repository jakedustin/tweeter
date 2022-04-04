package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IStoryDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class GetStoryService {
    IStoryDAO storyDAO;

    public GetStoryService(IDAOFactory factory) {
        storyDAO = factory.getStoryDAO();
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        try {
            Pair<List<StatusDTO>, Boolean> results;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new GetStoryResponse(e.getMessage());
        }
    }
}
