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
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.server.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask implements Runnable {

    private static final String LOG_TAG = "LoginTask";

    public static final String SUCCESS_KEY = "success";
    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private String username;
    /**
     * The user's password.
     */
    private String password;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public LoginTask(String username, String password, Handler messageHandler) {
        this.username = username;
        this.password = password;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            Pair<User, AuthToken> loginResult = doLogin();

            User loggedInUser = loginResult.getFirst();
            AuthToken authToken = loginResult.getSecond();

            BackgroundTaskUtils.loadImage(loggedInUser);

            sendSuccessMessage(loggedInUser, authToken);

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    private Pair<User, AuthToken> doLogin() throws IOException, TweeterRemoteException {
        LoginRequest loginRequest = new LoginRequest(this.username, this.password);
        ServerFacade serverFacade = new ServerFacade();
        LoginResponse loginResponse = serverFacade.login(loginRequest, "login");

        if (!loginResponse.isSuccess()) {
            this.sendFailedMessage(loginResponse.getMessage());
            return null;
        }

        User loggedInUser = loginResponse.getUser();
        AuthToken authToken = loginResponse.getAuthToken();
        return new Pair<>(loggedInUser, authToken);
    }

    private void sendSuccessMessage(User loggedInUser, AuthToken authToken) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, true);
        msgBundle.putSerializable(USER_KEY, loggedInUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);

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
