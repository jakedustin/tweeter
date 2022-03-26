package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements UserService.LogoutObserver,
        StatusService.StatusObserver,
        FollowService.IsFollowerObserver,
        FollowService.UnfollowUserObserver,
        FollowService.FollowObserver,
        FollowService.GetFollowersCountObserver,
        FollowService.GetFollowingCountObserver {
    private final View view;
    private final User currUser;
    private final FollowService followService = new FollowService();
    private final UserService userService = new UserService();
    private final StatusService statusService = new StatusService();
    private boolean parsingDisabled;

    public MainPresenter(View view) {
        this.view = view;
        this.currUser = Cache.getInstance().getCurrUser();
    }

    public MainPresenter(View view, User user, boolean isDisabled) {
        this.view = view;
        this.currUser = user;
        this.parsingDisabled = isDisabled;
    }

    public interface View {
        void displayMessage(String message);
        void clearMessage();
        void logoutUser();
        void isFollower(boolean isFollower);
        void updateSelectedUserFollowingAndFollowers();
        void updateFollowButton(boolean removed);
        void enableFollowButton(boolean enabled);
        void setFollowerCount(int count);
        void setFolloweeCount(int count);
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(selectedUser, this);
    }

    @Override
    public void isFollowerSucceeded(boolean isFollower) {
        view.isFollower(isFollower);
    }

    @Override
    public void isFollowerFailed(String errorMessage) {
        view.displayMessage(errorMessage);
    }

    @Override
    public void isFollowerThrew(Exception ex) {
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
    }

    public void followUser(User selectedUser) {
        view.displayMessage("Adding " + selectedUser.getName() + "...");
        followService.follow(selectedUser, this);
    }

    @Override
    public void followSucceeded() {
        view.updateSelectedUserFollowingAndFollowers();
        view.updateFollowButton(false);
        view.enableFollowButton(true);
    }

    @Override
    public void followFailed(String errorMessage) {
        view.displayMessage(errorMessage);
    }

    @Override
    public void followThrew(Exception ex) {
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
    }

    public void unfollowUser(User selectedUser) {
        view.displayMessage("Removing " + selectedUser.getName() + "...");
        followService.unfollowUser(selectedUser, this);
    }

    @Override
    public void unfollowUserSucceeded() {
        view.updateSelectedUserFollowingAndFollowers();
        view.updateFollowButton(true);
        view.enableFollowButton(true);
    }

    @Override
    public void unfollowUserFailed(String errorMessage) {
        view.displayMessage(errorMessage);
    }

    @Override
    public void unfollowUserThrew(Exception ex) {
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void postStatusSucceeded() {
        view.clearMessage();
        view.displayMessage("Successfully posted status");
    }

    @Override
    public void postStatusFailed(String errorMessage) {
        view.clearMessage();
        view.displayMessage(errorMessage);
    }

    @Override
    public void postStatusThrewException(Exception ex) {
        view.clearMessage();
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
    }

    public void postStatus(String post) {
        view.displayMessage("Posting Status...");
        try {
            Status newStatus = new Status(post, this.currUser, getFormattedDateTime(), parseURLs(post), parseMentions(post));
            getStatusService().postStatus(newStatus, this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void logoutUser() {
        view.displayMessage("Logging out...");
        getUserService().logout(this);
    }

    @Override
    public void logoutSucceeded() {
        view.displayMessage("Logged out successfully");
        Cache.getInstance().clearCache();
        view.logoutUser();
    }

    @Override
    public void logoutFailed(String errorMessage) {
        view.displayMessage(errorMessage);
    }

    @Override
    public void logoutThrewException(Exception ex) {
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        if (!parsingDisabled) {
            for (String word : post.split("\\s")) {
                if (word.startsWith("http://") || word.startsWith("https://")) {

                    int index = findUrlEndIndex(word);

                    word = word.substring(0, index);

                    containedUrls.add(word);
                }
            }
        }

        return containedUrls;
    }

    public void getFollowersCount(User selectedUser) {
        followService.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, this);
    }

    @Override
    public void getFollowersCountSucceeded(int count) {
        view.setFollowerCount(count);
    }

    @Override
    public void getFollowersCountFailed(String errorMessage) {
        view.displayMessage(errorMessage);
    }

    @Override
    public void getFollowersCountThrew(Exception ex) {
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
    }

    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, this);
    }

    @Override
    public void getFollowingCountSucceeded(int count) {
        view.setFolloweeCount(count);
    }

    @Override
    public void getFollowingCountFailed(String errorMessage) {
        view.displayMessage(errorMessage);
    }

    @Override
    public void getFollowingCountThrew(Exception ex) {
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        if (!parsingDisabled) {
            for (String word : post.split("\\s")) {
                if (word.startsWith("@")) {
                    word = word.replaceAll("[^a-zA-Z0-9]", "");
                    word = "@".concat(word);

                    containedMentions.add(word);
                }
            }
        }

        return containedMentions;
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa", Locale.US);

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public UserService getUserService() {
        return userService;
    }
    public StatusService getStatusService() {
        return statusService;
    }
}
