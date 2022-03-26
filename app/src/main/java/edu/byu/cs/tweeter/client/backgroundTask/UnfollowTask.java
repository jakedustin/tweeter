package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask implements Runnable {
    private static final String LOG_TAG = "UnfollowTask";

    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * Auth token for logged-in user.
     * This user is the "follower" in the relationship.
     */
    private final AuthToken authToken;
    /**
     * The user that is being followed.
     */
    private final User followee;
    /**
     * The current user.
     */
    private final User currentUser = Cache.getInstance().getCurrUser();
    /**
     * Message handler that will receive task results.
     */
    private final Handler messageHandler;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        this.authToken = authToken;
        this.followee = followee;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            UnfollowResponse response = unfollow(this.authToken, this.currentUser, this.followee);
            sendSuccessMessage();

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    private UnfollowResponse unfollow(AuthToken authToken, User currentUser, User followee) throws IOException, TweeterRemoteException {
        UnfollowRequest request = new UnfollowRequest(authToken, currentUser.getAlias(), followee.getAlias());
        ServerFacade server = new ServerFacade();
        return server.unfollow(request, "unfollow");
    }

    private void sendSuccessMessage() {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, true);

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
