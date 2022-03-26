package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter implements UserService.GetUserObserver, FollowService.GetFollowingObserver {

    private final String TAG = "FollowingPresenter";
    private final int PAGE_SIZE = 10;
    private User user;
    private User lastFollowee;
    private final AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();
    private boolean hasMorePages = true;
    private boolean isLoading = false;
    View view;
    FollowService followService = new FollowService();
    UserService userService = new UserService();

    public FollowingPresenter(View view) {
        this.view = view;
    }

    public interface View {
        void displayMessage(String message);
        void navigateToUser(User user);
        void addItems(List<User> followees);
        void setLoading(boolean isLoading);
    }

    /****************************************************************/
    /**                       FOLLOWSERVICE                        **/
    /****************************************************************/

    @Override
    public void getFollowingSucceeded(List<User> users, boolean hasMorePages, User lastFollowee) {
        this.lastFollowee = lastFollowee;
        this.hasMorePages = hasMorePages;
        this.isLoading = false;
        view.setLoading(false);
        view.addItems(users);
    }

    @Override
    public void getFollowingFailed(String failMessage) {
        view.displayMessage(failMessage);
        view.setLoading(false);
        this.isLoading = false;
    }

    @Override
    public void getFollowingThrew(Exception ex) {
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
        view.setLoading(false);
        this.isLoading = false;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            this.isLoading = true;
            view.setLoading(true);
            followService.getFollowing(authToken, user, PAGE_SIZE, lastFollowee, this);
        }
    }

    /****************************************************************/
    /**                         USERSERVICE                        **/
    /****************************************************************/

    @Override
    public void getUserSucceeded(User retrievedUser) {
        String METHOD_TAG = TAG + ".getUserSucceeded()";
        view.navigateToUser(retrievedUser);
        Log.i(METHOD_TAG, "successfully retrieved user " + user.toString());
    }

    @Override
    public void getUserFailed(String errorMessage) {
        String METHOD_TAG = TAG + ".getUserFailed";
        Log.w(METHOD_TAG, "failed to retrieve user: " + errorMessage);
    }

    @Override
    public void getUserThrewException(Exception ex) {
        String METHOD_TAG = TAG + ".getUserThrewException()";
        Log.e(METHOD_TAG, "fatal error in retrieving user: " + ex.getMessage());
    }

    public void getUser(String alias) {
        String METHOD_TAG = TAG + ".getUser()";
        userService.getUser(authToken, alias, this);
        Log.i(METHOD_TAG, "method returned " + user.toString());
    }

    public void setUser(User user) {
        String METHOD_TAG = TAG + ".setUser()";
        Log.i(METHOD_TAG, "user set as " + user.toString());
        this.user = user;
    }
}
