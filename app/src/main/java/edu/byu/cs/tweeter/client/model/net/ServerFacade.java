package edu.byu.cs.tweeter.client.model.net;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterUserResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Acts as a Facade to the Tweeter server. All network requests to the server should go through
 * this class.
 */
public class ServerFacade {

    private static final String SERVER_URL = "https://x0ixhlstd7.execute-api.us-west-2.amazonaws.com/dev";

    private final ClientCommunicator clientCommunicator = new ClientCommunicator(SERVER_URL);

    /**
     * Performs a registration and if successful, returns the logged in user and an auth token.
     *
     * @param request contains all information needed to perform a registration.
     * @return the registration response.
     */
    public RegisterUserResponse register(RegisterUserRequest request, String urlPath) throws IOException, TweeterRemoteException {
        RegisterUserResponse response = clientCommunicator.doPost(urlPath, request, null, RegisterUserResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Performs a login and if successful, returns the logged in user and an auth token.
     *
     * @param request contains all information needed to perform a login.
     * @return the login response.
     */
    public LoginResponse login(LoginRequest request, String urlPath) throws IOException, TweeterRemoteException {
        LoginResponse response = clientCommunicator.doPost(urlPath, request, null, LoginResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Performs a logout and if successful, returns the logged in user and an auth token.
     *
     * @param request contains all information needed to perform a logout.
     * @return the logout response.
     */
    public LogoutResponse logout(LogoutRequest request, String urlPath) throws IOException, TweeterRemoteException {
        LogoutResponse response = clientCommunicator.doPost(urlPath, request, null, LogoutResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public PostStatusResponse postStatus(PostStatusRequest request, String urlPath) throws IOException, TweeterRemoteException {
        PostStatusResponse response = clientCommunicator.doPost(urlPath, request, null, PostStatusResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public GetUserResponse getUser(GetUserRequest request, String urlPath)
            throws IOException, TweeterRemoteException {
        GetUserResponse response = clientCommunicator.doPost(urlPath, request, null, GetUserResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Adds the current user to the followers of the targeted user; Adds the targeted user to the
     * list of users the current user is following.
     *
     * @param request contains the AuthToken for the current user and the user object that is to be
     *                followed.
     *
     * @return an object describing the operation.
     */
    public FollowResponse follow(FollowRequest request, String urlPath) throws IOException, TweeterRemoteException {
        FollowResponse response = clientCommunicator.doPost(urlPath, request, null, FollowResponse.class);

        if (response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest request, String urlPath) throws IOException, TweeterRemoteException {
        UnfollowResponse response = clientCommunicator.doPost(urlPath, request, null, UnfollowResponse.class);

        if (response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowingResponse getFollowing(FollowingRequest request, String urlPath)
            throws IOException, TweeterRemoteException {

        FollowingResponse response = clientCommunicator.doPost(urlPath, request, null, FollowingResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowersResponse getFollowers(FollowersRequest request, String urlPath)
            throws IOException, TweeterRemoteException {

        FollowersResponse response = clientCommunicator.doPost(urlPath, request, null, FollowersResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Returns the count of users that the user specified in the request is following.
     *
     * @param request contains information about the user whose following count is to be returned
     *                and any other information required for the request.
     * @return the count of followees.
     */
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request, String urlPath)
            throws IOException, TweeterRemoteException {

        GetFollowingCountResponse response = clientCommunicator.doPost(urlPath, request, null, GetFollowingCountResponse.class);

        if (response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Returns the count of users that are following the user specified in the request
     *
     * @param request contains information about the user whose follower count is to be returned
     *                and any other information required for the request.
     * @return the count of followers.
     */
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request, String urlPath)
            throws IOException, TweeterRemoteException {

        GetFollowersCountResponse response = clientCommunicator.doPost(urlPath, request, null, GetFollowersCountResponse.class);

        if (response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Returns a boolean regarding the status of the relationship between the target user and the
     * current user.
     *
     * @param request contains information about the current user and the target user
     *
     * @return true if the current user is following the target user, else false
     */
    public IsFollowerResponse isFollower(IsFollowerRequest request, String urlPath)
            throws IOException, TweeterRemoteException {
        IsFollowerResponse response = clientCommunicator.doPost(urlPath, request, null, IsFollowerResponse.class);

        if (response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Fetches the story of the target user.
     *
     * @param request contains information about the target user and previous requests for
     *                their story.
     * @return a list of statuses corresponding to the current user
     */
    public GetStoryResponse getStory(GetStoryRequest request, String urlPath)
            throws IOException, TweeterRemoteException {
        GetStoryResponse response = clientCommunicator.doPost(urlPath, request, null, GetStoryResponse.class);

        if (response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Fetches the feed of the target user.
     *
     * @param request contains information about the target user and previous requests for their
     *                feed.
     *
     * @return a list of statuses corresponding to the current user's feed.
     */
    public GetFeedResponse getFeed(GetFeedRequest request, String urlPath) throws IOException, TweeterRemoteException {
        GetFeedResponse response = clientCommunicator.doPost(urlPath, request, null, GetFeedResponse.class);

        if (response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }
}