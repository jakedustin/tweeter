package edu.byu.cs.tweeter.client.presenter;

import java.util.logging.Logger;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Needs to implement the Observer interface so the presenter can call the service
 */
public class LoginPresenter implements UserService.LoginObserver {

    Logger logger = Logger.getLogger(this.getClass().toString());
    private final View view;

    public LoginPresenter(View view) {

        this.view = view;
    }

    private String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        return null;
    }

    public void login(String alias, String password) {
        view.clearErrorMessage();
        view.clearInfoMessage();

        String message = validateLogin(alias, password);
        if (message == null) {
            view.displayInfoMessage("Logging In...");
            new UserService().login(alias, password, this);
        } else {
            view.displayErrorMessage(message);
        }
    }

    /**
     * Every presenter will have a view interface containing the methods the view has to
     * implement.
     *
     * Have the views invoke the new activities.
     */
    public interface View {

        void navigateToUser(User user);

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void displayInfoMessage(String message);
        void clearInfoMessage();

    }

    @Override
    public void loginSucceeded(AuthToken authToken, User loggedInUser) {
        try {
            view.navigateToUser(loggedInUser);
            view.clearErrorMessage();
            view.displayInfoMessage("Hello " + loggedInUser.getAlias());
        } catch (Exception e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    @Override
    public void loginFailed(String message) {
        view.displayErrorMessage("Login failed: " + message);
    }

    @Override
    public void loginThrewException(Exception ex) {
        view.displayErrorMessage("Login failed: " + ex.getMessage());
    }


}
