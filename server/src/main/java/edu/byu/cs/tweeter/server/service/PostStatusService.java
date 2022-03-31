package edu.byu.cs.tweeter.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;

public class PostStatusService {
    private final IAuthTokenDAO authTokenDAO;
    private final IFollowDAO followDAO;
    private final IUserDAO userDAO;

    public PostStatusService(IDAOFactory factory) {
        this.authTokenDAO = factory.getAuthTokenDAO();
        this.followDAO = factory.getFollowDAO();
        this.userDAO = factory.getUserDAO();
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            List<String> followerAliases = followDAO.getAllFollowerAliases(request.getStatus().getUserAlias());
            Map<String, Object> status = new HashMap<>();
            status.put("alias", request.getStatus().getUserAlias());
            status.put("datetime", request.getStatus().getDatetime());
            status.put("urls", request.getStatus().getUrls());
            status.put("status", request.getStatus().getPost());
            status.put("mentions", request.getStatus().getMentions());

            userDAO.addPostToFeed(followerAliases, status);
            userDAO.addPostToStory(request.getStatus().getUserAlias(), status);
            return new PostStatusResponse(true);
        } catch (Exception ex) {
            return new PostStatusResponse(false, ex.getMessage());
        }
    }
}
