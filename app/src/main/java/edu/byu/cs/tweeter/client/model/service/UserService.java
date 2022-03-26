package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {

    private final String TAG = "UserService";

    public interface RegisterObserver {
        void registerSucceeded(AuthToken authToken, User registeredUser);
        void registerFailed(String errorMessage);
        void registerThrewException(Exception ex);
    }

    public void register(String firstName,
                         String lastName,
                         String alias,
                         String password,
                         String imageBytesBase64,
                         RegisterObserver registerObserver) {

        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new RegisterHandler(registerObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
    }

    private static class RegisterHandler extends Handler {
        private final RegisterObserver registerObserver;

        public RegisterHandler(RegisterObserver observer) {
            this.registerObserver = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(RegisterTask.SUCCESS_KEY);
            if (success) {
                User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

                Cache.getInstance().setCurrUser(registeredUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);
                registerObserver.registerSucceeded(authToken, registeredUser);
            } else if (msg.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(RegisterTask.MESSAGE_KEY);
                registerObserver.registerFailed(message);
            } else if (msg.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
                registerObserver.registerThrewException(ex);
            }
        }
    }

    public interface LoginObserver {
        void loginSucceeded(AuthToken authToken, User loggedInUser);
        void loginFailed(String errorMessage);
        void loginThrewException(Exception ex);
    }

    /**
     * Logs a user into the app
     *
     * @param alias the alias of the user attempting to log in
     * @param password the password of the user attempting to log in
     * @param observer the observer interface associated with the login request, will notify the
     *                 presenter when the task has been completed
     */
    public void login(String alias, String password, LoginObserver observer) {
        String METHOD_TAG = TAG + ".login()";
        Log.i(METHOD_TAG, "attempting login for " + alias);
        LoginTask loginTask = new LoginTask(alias,
                password,
                new LoginHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     * Handler wants to call the observer and inform it about what happened
     * with the submitted login
     *
     * Catch the notification, propagate it
     */
    private static class LoginHandler extends Handler {
        private final LoginObserver loginObserver;

        public LoginHandler(LoginObserver observer) {
            this.loginObserver = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

                Cache.getInstance().setCurrUser(loggedInUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                loginObserver.loginSucceeded(authToken, loggedInUser);
            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);
                loginObserver.loginFailed(message);
            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);
                loginObserver.loginThrewException(ex);
            }
        }
    }

    public interface LogoutObserver {
        void logoutSucceeded();
        void logoutFailed(String errorMessage);
        void logoutThrewException(Exception ex);
    }

    public void logout(LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }

    private class LogoutHandler extends Handler {
        LogoutObserver observer;

        public LogoutHandler(LogoutObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LogoutTask.SUCCESS_KEY);
            if (success) {
                observer.logoutSucceeded();
            } else if (msg.getData().containsKey(LogoutTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LogoutTask.MESSAGE_KEY);
                observer.logoutFailed(message);
            } else if (msg.getData().containsKey(LogoutTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LogoutTask.EXCEPTION_KEY);
                observer.logoutThrewException(ex);
            }
        }
    }

    /**
     * Implementable interface for presenters accessing the service.
     * One of three options:
     * 1. the request is successful and a user object is returned
     * 2. the request is not successful and an error message is returned
     * 3. the request causes an exception to be thrown and an exception is returned
     */
    public interface GetUserObserver {
        void getUserSucceeded(User retrievedUser);
        void getUserFailed(String errorMessage);
        void getUserThrewException(Exception ex);
    }

    /**
     * Retrieves a user object for viewing within the app
     *
     * @param authToken the authToken of the user currently logged in
     * @param alias the alias of the requested user object
     */
    public void getUser(AuthToken authToken, String alias, GetUserObserver observer) {
        String METHOD_TAG = TAG + ".getUser()";
        Log.i(METHOD_TAG, "attempting to get user " + alias);
        GetUserTask getUserTask = new GetUserTask(
                authToken,
                alias,
                new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);
        Log.i(METHOD_TAG, "method finished");
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private static class GetUserHandler extends Handler {
        private final GetUserObserver getUserObserver;

        /**
         * <h3>GetUserHandler Constructor</h3>
         * <p>Constructs and creates a GetUserHandler object. Sets the internal GetUserObserver.</p>
         * <p>This works because technically all the GetUserHandler has is a pointer to the
         *    observer; the methods it calls belong to the same instance that called it.</p>
         *
         * @param observer observer set by the FollowingPresenter when creating the handler
         */
        public GetUserHandler(GetUserObserver observer) {
            this.getUserObserver = observer;
        }

        /**
         * <p>Calls the observer from the layer above (observer implemented by presenter)</p>
         *
         * @param msg serialized objects containing information about the GetUserTask
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
                getUserObserver.getUserSucceeded(user);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                getUserObserver.getUserFailed(message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                getUserObserver.getUserThrewException(ex);
            }
        }
    }
}
