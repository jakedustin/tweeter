package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.FollowersPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.client.view.main.following.FollowingFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {
    private final String TAG = "FollowService";

    public interface GetFollowersObserver {
        void getFollowersSucceeded(List<User> users, boolean hasMorePages, User lastFollowee);

        void getFollowersFailed(String failMessage);

        void getFollowersThrew(Exception ex);
    }

    /**
     * <p>liveUsers := the list of users currently available in the presenter</p>
     * <p>newUsers := the list of users retrieved by getFollowing() method</p>
     * <p>sizeOf(x) := the number of distinct objects contained in x</p>
     *
     * <p>requires inputs are not null</p>
     * <p>requires valid authToken</p>
     *
     * <p>ensures sizeOf(liveUsers \cap newUsers) == 0</p>
     * <p>ensures sizeOf(liveUsers \cup newUsers) == sizeOf(liveUsers) + sizeOf(newUsers)</p>
     *
     * @param authToken                     the authToken of the user logged in to the app
     * @param targetUser                    the user for whom we are fetching followees
     * @param numUsersToFetch               the max number of users to return
     * @param lastFollowerFetchedPreviously the final followee returned last time
     * @param getFollowersObserver          the object implementing the observer interface that will
     *                                      action the message provided
     */
    public void getFollowers(AuthToken authToken,
                             User targetUser,
                             int numUsersToFetch,
                             User lastFollowerFetchedPreviously,
                             GetFollowersObserver getFollowersObserver) {
        String METHOD_TAG = TAG + ".getFollowing()";
        Log.i(METHOD_TAG, "method called");

        GetFollowersTask getFollowersTask = new GetFollowersTask(
                authToken,
                targetUser,
                numUsersToFetch,
                lastFollowerFetchedPreviously,
                new GetFollowersHandler(getFollowersObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends Handler {
        GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);

                User lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;

                observer.getFollowersSucceeded(followers, hasMorePages, lastFollower);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.getFollowersFailed(message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.getFollowersThrew(ex);
            }
        }
    }

    /**
     * <p>liveUsers := the list of users currently available in the presenter</p>
     * <p>newUsers := the list of users retrieved by getFollowing() method</p>
     * <p>sizeOf(x) := the number of distinct objects contained in x</p>
     *
     * <p>requires inputs are not null</p>
     * <p>requires valid authToken</p>
     *
     * <p>ensures sizeOf(liveUsers \cap newUsers) == 0</p>
     * <p>ensures sizeOf(liveUsers \cup newUsers) == sizeOf(liveUsers) + sizeOf(newUsers)</p>
     *
     * @param authToken                     the authToken of the user logged in to the app
     * @param targetUser                    the user for whom we are fetching followees
     * @param numUsersToFetch               the max number of users to return
     * @param lastFolloweeFetchedPreviously the final followee returned last time
     * @param getFollowingObserver          the object implementing the observer interface that will
     *                                      action the message provided
     */
    public void getFollowing(AuthToken authToken,
                             User targetUser,
                             int numUsersToFetch,
                             User lastFolloweeFetchedPreviously,
                             GetFollowingObserver getFollowingObserver) {
        String METHOD_TAG = TAG + ".getFollowing()";
        Log.i(METHOD_TAG, "method called");

        GetFollowingTask getFollowingTask = new GetFollowingTask(
                authToken,
                targetUser,
                numUsersToFetch,
                lastFolloweeFetchedPreviously,
                new GetFollowingHandler(getFollowingObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }

    public interface GetFollowingObserver {
        void getFollowingSucceeded(List<User> users, boolean hasMorePages, User lastFollowee);

        void getFollowingFailed(String failMessage);

        void getFollowingThrew(Exception ex);
    }

    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends Handler {
        GetFollowingObserver observer;

        public GetFollowingHandler(GetFollowingObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                User lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;

                observer.getFollowingSucceeded(followees, hasMorePages, lastFollowee);
            } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
                observer.getFollowingFailed(message);
            } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.getFollowingThrew(ex);
            }
        }
    }

    public interface IsFollowerObserver {
        void isFollowerSucceeded(boolean isFollower);

        void isFollowerFailed(String errorMessage);

        void isFollowerThrew(Exception ex);
    }

    public void isFollower(User selectedUser, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    private class IsFollowerHandler extends Handler {
        IsFollowerObserver observer;

        public IsFollowerHandler(IsFollowerObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
            if (success) {
                boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

                // presenter will call View.setFollowing(boolean)
                // If logged in user if a follower of the selected user, display the follow button as "following"
                observer.isFollowerSucceeded(isFollower);
            } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
                String message = "Failed to determine following relationship: " + msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
                observer.isFollowerFailed(message);
            } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
                observer.isFollowerThrew(ex);
            }
        }
    }

    public interface FollowObserver {
        void followSucceeded();
        void followFailed(String errorMessage);
        void followThrew(Exception ex);
    }

    public void follow(User selectedUser, FollowObserver observer) {
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    // FollowHandler

    private class FollowHandler extends Handler {
        FollowObserver observer;

        public FollowHandler(FollowObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                observer.followSucceeded();
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = "Failed to follow: " + msg.getData().getString(FollowTask.MESSAGE_KEY);
                observer.followFailed(message);
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.followThrew(ex);
            }
        }
    }

    public interface UnfollowUserObserver {
        void unfollowUserSucceeded();
        void unfollowUserFailed(String errorMessage);
        void unfollowUserThrew(Exception ex);
    }

    public void unfollowUser(User selectedUser, UnfollowUserObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new UnfollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    private static class UnfollowHandler extends Handler {
        UnfollowUserObserver observer;

        public UnfollowHandler(UnfollowUserObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                observer.unfollowUserSucceeded();
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = "Failed to unfollow: " + msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                observer.unfollowUserFailed(message);
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                observer.unfollowUserThrew(ex);
            }
        }
    }

    public interface GetFollowersCountObserver {
        void getFollowersCountSucceeded(int count);
        void getFollowersCountFailed(String errorMessage);
        void getFollowersCountThrew(Exception ex);
    }

    public void getFollowersCount(AuthToken authToken,
                                  User user,
                                  GetFollowersCountObserver observer) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken,
                user,
                new GetFollowersCountHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followersCountTask);
    }

    // GetFollowersCountHandler

    private static class GetFollowersCountHandler extends Handler {
        GetFollowersCountObserver observer;

        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.getFollowersCountSucceeded(count);
            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.getFollowersCountFailed(message);
            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.getFollowersCountThrew(ex);
            }
        }
    }

    public interface GetFollowingCountObserver {
        void getFollowingCountSucceeded(int count);
        void getFollowingCountFailed(String errorMessage);
        void getFollowingCountThrew(Exception ex);
    }

    public void getFollowingCount(AuthToken authToken,
                                  User user,
                                  GetFollowingCountObserver observer) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken,
                user,
                new GetFollowingCountHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followingCountTask);
    }

    // GetFollowingCountHandler

    private static class GetFollowingCountHandler extends Handler {
        GetFollowingCountObserver observer;

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                observer.getFollowingCountSucceeded(count);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                observer.getFollowingCountFailed(message);
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
                observer.getFollowingCountThrew(ex);
            }
        }
    }
}
