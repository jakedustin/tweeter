package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.util.Pair;

public interface IFollowDAO {
    Pair<List<String>, Boolean> getFollowing(String followerAlias, int limit, String lastFolloweeAlias);
    Pair<List<String>, Boolean> getFollowers(String followeeAlias, int limit, String lastFollowerAlias);
    GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request);
    GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request);
    IsFollowerResponse isFollower(IsFollowerRequest request);
    FollowResponse follow(String followerAlias, String followeeAlias);
    UnfollowResponse unfollow(String followerAlias, String followeeAlias);
    void addFollowersBatch(List<String> userAliases, String followTarget);

}
