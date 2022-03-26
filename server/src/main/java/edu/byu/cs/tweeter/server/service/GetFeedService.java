package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.GetFeedDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFeedDAO;

public class GetFeedService {
    private IFeedDAO feedDAO;

    public GetFeedService(IDAOFactory factory) {
        feedDAO = factory.getFeedDAO();
    }

    public GetFeedResponse getFeed(GetFeedRequest request) {
        try {
            GetFeedResponse response = feedDAO.getFeed(request);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private GetFeedDAO getFeedDAO() {
        return new GetFeedDAO();
    }
}
