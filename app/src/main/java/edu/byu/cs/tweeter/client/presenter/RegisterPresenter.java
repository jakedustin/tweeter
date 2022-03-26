package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Needs to implement the Observer interface so the presenter can call the service
 */
public class RegisterPresenter implements UserService.RegisterObserver {

    UserService userService = new UserService();
    Logger logger = Logger.getLogger(this.getClass().toString());
    private final View view;

    public interface View {

        void navigateToUser(User user);

        void displayMessage(String message);

        void displayErrorMessage(String message);
        void clearErrorMessage();
    }

    public RegisterPresenter(View view) {
        this.view = view;
    }

    public void registerUser(EditText _firstName,
                             EditText _lastName,
                             EditText _alias,
                             EditText _password,
                             ImageView _imageToUpload) {
        view.clearErrorMessage();

        String firstName = _firstName.getText().toString();
        String lastName = _lastName.getText().toString();
        String alias = _alias.getText().toString();
        String password = _password.getText().toString();

        try {
            validateRegistration(
                    firstName,
                    lastName,
                    alias,
                    password,
                    _imageToUpload.getDrawable());

            String imageAsBytes = convertImageToBytes(_imageToUpload);
            userService.register(firstName, lastName, alias, password, imageAsBytes, this);

        } catch (IllegalArgumentException ex) {
            view.displayErrorMessage(ex.getMessage());
        }
    }

    public void validateRegistration(String firstName,
                                     String lastName,
                                     String alias,
                                     String password,
                                     Drawable image) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (image == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

    String convertImageToBytes(ImageView imageToConvert) {
        Bitmap image = ((BitmapDrawable) imageToConvert.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    @Override
    public void registerSucceeded(AuthToken authToken, User registeredUser) {
        view.navigateToUser(registeredUser);
        view.displayMessage("Hello " + registeredUser.getAlias());
    }

    @Override
    public void registerFailed(String message) {
        view.displayMessage("Register failed: " + message);
    }

    @Override
    public void registerThrewException(Exception ex) {
        view.displayMessage("Register failed: " + ex.getMessage());
        ex.printStackTrace();
    }


}
