package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask implements Runnable {
    private static final String LOG_TAG = "GetFollowingTask";

    public static final String SUCCESS_KEY = "success";
    public static final String FOLLOWEES_KEY = "followees";
    public static final String MORE_PAGES_KEY = "more-pages";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * The user whose following is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser;
    /**
     * Maximum number of followed users to return (i.e., page size).
     */
    private int limit;
    /**
     * The last person being followed returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private User lastFollowee;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastFollowee = lastFollowee;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            Pair<List<User>, Boolean> pageOfUsers = getFollowees();

            List<User> followees = pageOfUsers.getFirst();
            boolean hasMorePages = pageOfUsers.getSecond();

            loadImages(followees);

            sendSuccessMessage(followees, hasMorePages);

        } catch (Exception ex) {
            Log.e(LOG_TAG, "Failed to get followees", ex);
            sendExceptionMessage(ex);
        }
    }

    private Pair<List<User>, Boolean> getFollowees() throws IOException, TweeterRemoteException {
        String alias = null;
        if (this.lastFollowee != null) {
            alias = this.lastFollowee.getAlias();
        }
        FollowingRequest followingRequest = new FollowingRequest(this.authToken, this.targetUser.getAlias(), this.limit, alias);
        ServerFacade serverFacade = new ServerFacade();
        FollowingResponse followingResponse = serverFacade.getFollowing(followingRequest, "getfollowing");
        return new Pair<>(followingResponse.getFollowees(), followingResponse.getHasMorePages());
    }

    private void loadImages(List<User> followees) throws IOException {
        for (User u : followees) {
            BackgroundTaskUtils.loadImage(u);
        }
    }

    private void sendSuccessMessage(List<User> followees, boolean hasMorePages) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, true);
        msgBundle.putSerializable(FOLLOWEES_KEY, (Serializable) followees);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);

        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }

    private void sendFailedMessage(String message) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, false);
        msgBundle.putString(MESSAGE_KEY, message);

        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }

    private void sendExceptionMessage(Exception exception) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, false);
        msgBundle.putSerializable(EXCEPTION_KEY, exception);

        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }

}
