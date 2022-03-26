package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterUserResponse extends Response {

    private User user;
    private AuthToken authToken;

    RegisterUserResponse(boolean success) {
        super(success);
    }

    public RegisterUserResponse(boolean success, String message) {
        super(success, message);
    }

    public RegisterUserResponse(User user, AuthToken authToken) {
        super(true, null);
        this.user = user;
        this.authToken = authToken;
    }

    public User getUser() {
        return user;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setUser(User user) {
        this.user=user;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken=authToken;
    }

    @Override
    public String toString() {
        return "RegisterUserResponse{" +
                "user=" + user +
                ", authToken=" + authToken +
                '}';
    }
}
