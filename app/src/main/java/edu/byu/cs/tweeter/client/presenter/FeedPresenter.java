package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements UserService.GetUserObserver, FeedService.GetFeedObserver {
    private View view;
    private final UserService userService = new UserService();
    private final FeedService feedService = new FeedService();
    private final AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();
    private final int PAGE_SIZE = 10;
    private Status lastStatus;
    private boolean isLoading = false;
    private boolean hasMoreItems = true;

    public FeedPresenter(View view) {
        this.view = view;
    }

    public interface View {
        void addItems(List<Status> statusList);
        void navigateToUser(User user);
        void displayMessage(String message);
        void setLoading(boolean isLoading);
    }

    @Override
    public void getFeedSucceeded(List<Status> statusList, Status lastStatus, boolean hasMoreItems) {
        this.lastStatus = lastStatus;
        this.hasMoreItems = hasMoreItems;
        this.isLoading = false;
        view.setLoading(false);
        view.addItems(statusList);
    }

    @Override
    public void getFeedFailed(String errorMessage) {
        view.displayMessage(errorMessage);
        view.setLoading(false);
        this.isLoading = false;
    }

    @Override
    public void getFeedThrew(Exception ex) {
        view.displayMessage(ex.getMessage());
        ex.printStackTrace();
        view.setLoading(false);
        this.isLoading = false;
    }

    public void loadMoreItems(User user) {
        if (!isLoading && hasMoreItems) {
            this.isLoading = true;
            view.setLoading(true);
            user.setImageBytes(null);
            feedService.getFeedItems(authToken, user, PAGE_SIZE, lastStatus, this);
        }
    }

    @Override
    public void getUserSucceeded(User retrievedUser) {
        view.setLoading(false);
        view.navigateToUser(retrievedUser);
    }

    @Override
    public void getUserFailed(String errorMessage) {
        view.setLoading(false);
        view.displayMessage(errorMessage);
    }

    @Override
    public void getUserThrewException(Exception ex) {
        view.setLoading(false);
        view.displayMessage(ex.getMessage());
    }

    public void getUser(String alias, AuthToken authToken) {
        userService.getUser(authToken, alias, this);
    }
}
