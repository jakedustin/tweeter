package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;

import java.util.Objects;

/**
 * A response for a {@link GetFollowingCountRequest}
 */
public class GetFollowingCountResponse extends Response {

    private int numFollowing;

    /**
     * Creates a response indicating the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful
     */
    public GetFollowingCountResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param numFollowing the number of followees included in the result.
     */
    public GetFollowingCountResponse(int numFollowing) {
        super(true);
        this.numFollowing = numFollowing;
    }

    public int getNumFollowing() {
        return numFollowing;
    }

    public void setNumFollowing(int numFollowing) {
        this.numFollowing = numFollowing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetFollowingCountResponse that = (GetFollowingCountResponse) o;
        return numFollowing == that.numFollowing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numFollowing);
    }
}
