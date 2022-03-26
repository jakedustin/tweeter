package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response {

    private boolean isFollower;

    public IsFollowerResponse() {
        super(false);
    }

    public IsFollowerResponse(boolean success) {
        super(success);
    }

    public IsFollowerResponse(boolean success, String message) {
        super(success, message);
    }

    public IsFollowerResponse(boolean success, boolean isFollower) {
        super(success);
        this.isFollower = isFollower;
    }

    public boolean getIsFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean follower) {
        isFollower = follower;
    }
}
