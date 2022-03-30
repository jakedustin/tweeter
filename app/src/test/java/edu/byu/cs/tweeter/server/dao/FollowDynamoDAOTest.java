package edu.byu.cs.tweeter.server.dao;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.UserDTO;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class FollowDynamoDAOTest {
    private final String DEFAULT_PROFILE_URL = "https://jd-cs340-profile-pictures.s3.us-west-2.amazonaws.com/%40default_profile";

    IFollowDAO followDao = new DynamoDAOFactory().getFollowDAO();
    IUserDAO userDao = new DynamoDAOFactory().getUserDAO();

    UserDTO testUserOne = new UserDTO("@testUserOne", "user", "one");
    UserDTO testUserTwo = new UserDTO("@testUserTwo", "user", "two");

    @BeforeEach
    void setup() {
        try {
            userDao.postNewUser(testUserOne.getUserAlias(), testUserOne.getFirstName(), testUserOne.getLastName(), DEFAULT_PROFILE_URL);
            userDao.postNewUser(testUserTwo.getUserAlias(), testUserTwo.getFirstName(), testUserTwo.getLastName(), DEFAULT_PROFILE_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void takedown() {
        try {
            userDao.deleteUsers(Arrays.asList(testUserOne.getUserAlias(), testUserTwo.getUserAlias()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFollowingTest_ensureThatAllFolloweesAreReturned() {
        String follower = "@testFollower";
        List<String> followees = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            followees.add("@testFollowee" + i);
        }

        for (int i = 0; i < followees.size(); ++i) {
            followDao.follow(follower, followees.get(i));
        }

        Pair<List<String>, Boolean> returnedGetFollowingResult = followDao.getFollowing(follower, 10, "");
        Assertions.assertEquals(10, returnedGetFollowingResult.getFirst().size());
        for (int i = 0; i < returnedGetFollowingResult.getFirst().size(); ++i) {
            System.out.println(returnedGetFollowingResult.getFirst().get(i));
        }
        Assertions.assertTrue(returnedGetFollowingResult.getFirst().containsAll(followees));
        for (String alias : followees) {
            followDao.unfollow(follower, alias);
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
            followDao.follow(followers.get(i), followee);
        }

        Pair<List<String>, Boolean> returnedGetFollowingResult =
                followDao.getFollowers(followee, 10, "");
        Assertions.assertEquals(10, returnedGetFollowingResult.getFirst().size());
        Assertions.assertTrue(returnedGetFollowingResult.getFirst().containsAll(followers));
        for (String alias : followers) {
            followDao.unfollow(alias, followee);
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
            followDao.follow(follower, followees.get(i));
        }

        Pair<List<String>, Boolean> returnedGetFollowingResult = followDao.getFollowing(follower, 10, "");
        for (int i = 0; i < 10; ++i) {
            Assertions.assertTrue(returnedGetFollowingResult.getFirst().contains(followees.get(i)));
        }
        Assertions.assertTrue(returnedGetFollowingResult.getSecond());

        returnedGetFollowingResult = followDao.getFollowing(follower, 10, returnedGetFollowingResult.getFirst().get(9));
        for (int i = 10; i < 20; ++i) {
            Assertions.assertTrue(returnedGetFollowingResult.getFirst().contains(followees.get(i)));
        }
        Assertions.assertFalse(returnedGetFollowingResult.getSecond());

        for (String alias : followees) {
            followDao.unfollow(follower, alias);
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
            followDao.follow(followers.get(i), followee);
        }

        Pair<List<String>, Boolean> returnedGetFollowingResult = followDao.getFollowers(followee, 10, "");
        for (int i = 0; i < 10; ++i) {
            System.out.println("result = " + returnedGetFollowingResult.getFirst().get(i));
            System.out.println("expected = " + followers.get(i));
            Assertions.assertTrue(returnedGetFollowingResult.getFirst().contains(followers.get(i)));
        }
        Assertions.assertTrue(returnedGetFollowingResult.getSecond());

        returnedGetFollowingResult = followDao.getFollowers(followee, 10, returnedGetFollowingResult.getFirst().get(9));
        for (int i = 10; i < 20; ++i) {
            System.out.println("returned " + returnedGetFollowingResult.getFirst().get(i - 10));
            System.out.println("expected " + followers.get(i));
            Assertions.assertTrue(returnedGetFollowingResult.getFirst().contains(followers.get(i)));
        }
        Assertions.assertFalse(returnedGetFollowingResult.getSecond());

        for (String alias : followers) {
            followDao.unfollow(alias, followee);
        }
    }

    @Test
    public void isFollowerTest_shouldReturnTrue_relationshipExists() {
        String followee = "@testFollowee";
        String follower = "@testFollower";

        followDao.follow(follower, followee);

        try {
            Assertions.assertTrue(followDao.isFollower(follower, followee));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        followDao.unfollow(follower, followee);
    }

    @Test
    public void isFollowerTest_shouldReturnFalse_relationshipDoesNotExist() {
        String followee = "@testFollowee";
        String follower = "@testFollower";

        try {
            Assertions.assertFalse(followDao.isFollower(follower, followee));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void getFollowingCount_shouldReturnCorrectCount_forNewUser() {
        // insert new user
        // add follows relationships
        // check values
        try {
            Assertions.assertEquals(0, followDao.getFollowingCount(testUserOne.getUserAlias()));
            Assertions.assertEquals(0, followDao.getFollowingCount(testUserTwo.getUserAlias()));

            followDao.follow(testUserOne.getUserAlias(), testUserTwo.getUserAlias());
            followDao.follow(testUserTwo.getUserAlias(), testUserOne.getUserAlias());

            Assertions.assertEquals(1, followDao.getFollowingCount(testUserOne.getUserAlias()));
            Assertions.assertEquals(1, followDao.getFollowingCount(testUserTwo.getUserAlias()));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void getFollowersCount_shouldReturnCorrectCount_forNewUser() {
        Assertions.assertEquals(0, followDao.getFollowersCount("@testFollower"));

    }

    @Test
    public void getFollowingCount_incrementCountWhenNewFollowRelationshipAdded() {

    }

    @Test
    public void getFollowingCount_decrementCountWhenFollowRelationshipRemoved() {

    }

    @Test
    public void getFollowersCount_incrementCountWhenNewFollowRelationshipAdded() {

    }

    @Test
    public void getFollowersCount_decrementCountWhenFollowRelationshipRemoved() {

    }
}