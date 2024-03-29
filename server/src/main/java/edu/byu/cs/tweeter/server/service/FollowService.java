package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class FollowService {
    private final IFollowDAO followDAO;
    private final IUserDAO userDAO;

    public FollowService(IDAOFactory factory) {
        this.followDAO = factory.getFollowDAO();
        this.userDAO = factory.getUserDAO();
    }

    public FollowResponse follow(FollowRequest request) {
        return followDAO.follow(request);
    }

    public FollowingResponse getFollowing(FollowingRequest request) {
        return followDAO.getFollowing(request);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        Pair<List<String>, Boolean> followers = followDAO.getFollowers(
                request.getFolloweeAlias(),
                request.getLimit(),
                request.getLastFollowerAlias()
        );

        List<User> users = new ArrayList<>(followers.getFirst().size());

        try {
            // TODO: make this better lol
            for (String alias : followers.getFirst()) {
                users.add(userDAO.getUser(alias));
            }

            // TODO: add a last user parameter to the followers response
            return new FollowersResponse(users, followers.getSecond());
        } catch (Exception e) {
            e.printStackTrace();
            return new FollowersResponse(e.getMessage());
        }
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        return followDAO.getFollowingCount(request);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        return followDAO.getFollowersCount(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        return followDAO.isFollower(request);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        return followDAO.unfollow(request);
    }
}
