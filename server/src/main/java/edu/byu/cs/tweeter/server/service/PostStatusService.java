package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IStatusDAO;

public class PostStatusService {
    private final IStatusDAO statusDAO;

    public PostStatusService(IDAOFactory factory) {
        this.statusDAO = factory.getStatusDAO();
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            return statusDAO.postStatus(request);
        } catch (Exception ex) {
            return new PostStatusResponse(false, ex.getMessage());
        }
    }
}
