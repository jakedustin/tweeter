package edu.byu.cs.tweeter.server.dao;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class FollowDynamoDAOTest {
    IFollowDAO dao = new DynamoDAOFactory().getFollowDAO();

    @Test
    public void getFollowingTest_ensureThatAllFolloweesAreReturned() {
        String follower = "@testFollower";
        List<String> followees = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            followees.add("@testFollowee" + i);
        }

        for (int i = 0; i < followees.size(); ++i) {
            dao.follow(follower, followees.get(i));
        }

        Pair<List<String>, Boolean> returnedGetFollowingResult = dao.getFollowing(follower, 10, "");
        Assertions.assertEquals(10, returnedGetFollowingResult.getFirst().size());
        for (int i = 0; i < returnedGetFollowingResult.getFirst().size(); ++i) {
            System.out.println(returnedGetFollowingResult.getFirst().get(i));
        }
        Assertions.assertTrue(returnedGetFollowingResult.getFirst().containsAll(followees));
        for (String alias : followees) {
            dao.unfollow(follower, alias);
        }
    }

    @Test
    public void getFollowersTest_ensureThatAllFollowersAreReturned() {
        String followee = "@testFollowee";
        List<String> followers = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            followers.add("@testFollower" + i);
        }

        for (int i = 0; i < followers.size(); ++i) {
            dao.follow(followers.get(i), followee);
        }

        Pair<List<String>, Boolean> returnedGetFollowingResult =
                dao.getFollowers(followee, 10, "");
        Assertions.assertEquals(10, returnedGetFollowingResult.getFirst().size());
        Assertions.assertTrue(returnedGetFollowingResult.getFirst().containsAll(followers));
        for (String alias : followers) {
            dao.unfollow(alias, followee);
        }
    }

    @Test
    public void getFollowingTest_ensureThatPaginationWorks() {
        String follower = "@testFollower";
        List<String> followees = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            followees.add("@testFollowee" + i);
        }

        Collections.sort(followees);

        for (int i = 0; i < followees.size(); ++i) {
            dao.follow(follower, followees.get(i));
        }

        Pair<List<String>, Boolean> returnedGetFollowingResult = dao.getFollowing(follower, 10, "");
        for (int i = 0; i < 10; ++i) {
            Assertions.assertTrue(returnedGetFollowingResult.getFirst().contains(followees.get(i)));
        }
        Assertions.assertTrue(returnedGetFollowingResult.getSecond());

        returnedGetFollowingResult = dao.getFollowing(follower, 10, returnedGetFollowingResult.getFirst().get(9));
        for (int i = 10; i < 20; ++i) {
            Assertions.assertTrue(returnedGetFollowingResult.getFirst().contains(followees.get(i)));
        }
        Assertions.assertFalse(returnedGetFollowingResult.getSecond());

        for (String alias : followees) {
            dao.unfollow(follower, alias);
        }
    }

    @Test
    public void getFollowersTest_ensureThatPaginationWorks() {
        String followee = "@testFollowee";
        List<String> followers = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            followers.add("@testFollower" + i);
        }

        Collections.sort(followers);

        for (int i = 0; i < followers.size(); ++i) {
            dao.follow(followers.get(i), followee);
        }

        Pair<List<String>, Boolean> returnedGetFollowingResult = dao.getFollowers(followee, 10, "");
        for (int i = 0; i < 10; ++i) {
            System.out.println("result = " + returnedGetFollowingResult.getFirst().get(i));
            System.out.println("expected = " + followers.get(i));
            Assertions.assertTrue(returnedGetFollowingResult.getFirst().contains(followers.get(i)));
        }
        Assertions.assertTrue(returnedGetFollowingResult.getSecond());

        returnedGetFollowingResult = dao.getFollowers(followee, 10, returnedGetFollowingResult.getFirst().get(9));
        for (int i = 10; i < 20; ++i) {
            System.out.println("returned " + returnedGetFollowingResult.getFirst().get(i - 10));
            System.out.println("expected " + followers.get(i));
            Assertions.assertTrue(returnedGetFollowingResult.getFirst().contains(followers.get(i)));
        }
        Assertions.assertFalse(returnedGetFollowingResult.getSecond());

        for (String alias : followers) {
            dao.unfollow(alias, followee);
        }
    }
}