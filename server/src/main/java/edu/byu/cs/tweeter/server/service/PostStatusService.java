package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;

public class PostStatusService {
    private final IAuthTokenDAO authTokenDAO;
    private final IFollowDAO followDAO;
    private final IUserDAO userDAO;
    private final IFeedDAO feedDAO;
    private final IStoryDAO storyDAO;

    public PostStatusService(IDAOFactory factory) {
        this.authTokenDAO = factory.getAuthTokenDAO();
        this.followDAO = factory.getFollowDAO();
        this.userDAO = factory.getUserDAO();
        this.feedDAO = factory.getFeedDAO();
        this.storyDAO = factory.getStoryDAO();
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            List<String> followerAliases = followDAO.getAllFollowerAliases(request.getStatus().getUserAlias());

            for (String user : followerAliases) {
                feedDAO.postStatusToFeed(request.getStatus(), user);
            }
            storyDAO.postStatusToStory(request.getStatus());
            return new PostStatusResponse(true);
        } catch (Exception ex) {
            return new PostStatusResponse(false, ex.getMessage());
        }
    }
}
