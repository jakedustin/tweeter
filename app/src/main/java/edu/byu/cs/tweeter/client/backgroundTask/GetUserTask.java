package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.util.ByteArrayUtils;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask implements Runnable {
    private static final String LOG_TAG = "GetUserTask";

    public static final String SUCCESS_KEY = "success";
    public static final String USER_KEY = "user";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private String alias;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        this.authToken = authToken;
        this.alias = alias;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            GetUserResponse response = getUser(this.authToken, this.alias);
            User responseUser = response.getUser();
            byte[] imageBytes = ByteArrayUtils.bytesFromUrl(response.getUser().getImageUrl());
            responseUser.setImageBytes(imageBytes);

            sendSuccessMessage(responseUser);

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    private FakeData getFakeData() {
        return new FakeData();
    }

    private GetUserResponse getUser(AuthToken authToken, String alias) throws IOException, TweeterRemoteException {
        GetUserRequest request = new GetUserRequest(authToken, alias);
        ServerFacade server = new ServerFacade();
        GetUserResponse response = server.getUser(request, "getuser");
        return response;
    }

    private void sendSuccessMessage(User user) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, true);
        msgBundle.putSerializable(USER_KEY, user);

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
