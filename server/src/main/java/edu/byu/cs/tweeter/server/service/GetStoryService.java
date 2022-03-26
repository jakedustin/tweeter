package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IStoryDAO;

public class GetStoryService {
    IStoryDAO storyDAO;

    public GetStoryService(IDAOFactory factory) {
        storyDAO = factory.getStoryDAO();
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        try {
            return storyDAO.getStory(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new GetStoryResponse(e.getMessage());
        }
    }
}
