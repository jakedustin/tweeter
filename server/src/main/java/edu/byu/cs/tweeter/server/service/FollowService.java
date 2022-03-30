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
import edu.byu.cs.tweeter.server.dao.interfaces.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class FollowService {
    private final IFollowDAO followDAO;
    private final IUserDAO userDAO;
    private final IAuthTokenDAO authTokenDAO;

    public FollowService(IDAOFactory factory) {
        this.followDAO = factory.getFollowDAO();
        this.userDAO = factory.getUserDAO();
        this.authTokenDAO = factory.getAuthTokenDAO();
    }

    public FollowResponse follow(FollowRequest request) {
        // need method to increment and decrement values
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            userDAO.adjustFollowingValue(true, request.getFollowerAlias());
            userDAO.adjustFollowersValue(true, request.getFolloweeAlias());
            return followDAO.follow(request.getFollowerAlias(), request.getFolloweeAlias());
        } catch (Exception e) {
            return new FollowResponse(false, e.getMessage());
        }
    }

    public FollowingResponse getFollowing(FollowingRequest request) {
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
        } catch (Exception e) {
            return new FollowingResponse(e.getMessage());
        }
        Pair<List<String>, Boolean> returnedValues = followDAO.getFollowing(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
        List<User> followees = new ArrayList<>();
        try {
            for (String userHandle : returnedValues.getFirst()) {
                followees.add(userDAO.getUser(userHandle));
            }
        } catch (Exception e) {
            return new FollowingResponse(e.getMessage());
        }
        return new FollowingResponse(followees, returnedValues.getSecond());
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        // verify auth token
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
        } catch (Exception e) {
            return new FollowersResponse(e.getMessage());
        }

        // get list of follower aliases
        Pair<List<String>, Boolean> followers = followDAO.getFollowers(
                request.getFolloweeAlias(),
                request.getLimit(),
                request.getLastFollowerAlias()
        );

        // get list of users using the aliases
        List<User> users = new ArrayList<>(followers.getFirst().size());

        try {
            for (String alias : followers.getFirst()) {
                users.add(userDAO.getUser(alias));
            }

            return new FollowersResponse(users, followers.getSecond());
        } catch (Exception e) {
            e.printStackTrace();
            return new FollowersResponse(e.getMessage());
        }
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        int followingCount;
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            followingCount = followDAO.getFollowingCount(request.getFollowerAlias());
        } catch (Exception e) {
            return new GetFollowingCountResponse(e.getMessage());
        }
        return new GetFollowingCountResponse(followingCount);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        int followersCount;
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            followersCount = followDAO.getFollowersCount(request.getFolloweeAlias());
        } catch (Exception e) {
            return new GetFollowersCountResponse(e.getMessage());
        }
        return new GetFollowersCountResponse(followersCount);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        boolean isFollower;
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            isFollower = followDAO.isFollower(request.getFollowerAlias(), request.getFolloweeAlias());
        } catch (Exception e) {
            return new IsFollowerResponse(false, e.getMessage());
        }
        return new IsFollowerResponse(isFollower);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            userDAO.adjustFollowingValue(false, request.getFollowerAlias());
            userDAO.adjustFollowersValue(false, request.getFolloweeAlias());
            return followDAO.unfollow(request.getFollowerAlias(), request.getFolloweeAlias());
        } catch (Exception e) {
            return new UnfollowResponse(false, e.getMessage());
        }
    }
}
