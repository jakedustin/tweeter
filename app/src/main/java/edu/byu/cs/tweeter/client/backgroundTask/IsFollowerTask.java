package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask implements Runnable {
    private static final String LOG_TAG = "IsFollowerTask";

    public static final String SUCCESS_KEY = "success";
    public static final String IS_FOLLOWER_KEY = "is-follower";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * The alleged follower.
     */
    private User follower;
    /**
     * The alleged followee.
     */
    private User followee;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        this.authToken = authToken;
        this.follower = follower;
        this.followee = followee;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {

            IsFollowerResponse response = isFollower(this.authToken, this.follower, this.followee);
            sendSuccessMessage(response.getIsFollower());

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    private IsFollowerResponse isFollower(AuthToken authToken, User follower, User followee) throws IOException, TweeterRemoteException {
        IsFollowerRequest request = new IsFollowerRequest(authToken, follower.getAlias(), followee.getAlias());
        ServerFacade server = new ServerFacade();
        return server.isFollower(request, "isfollower");
    }

    private void sendSuccessMessage(boolean isFollower) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, true);
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);

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
