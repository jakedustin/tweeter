package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;

import java.util.Objects;

/**
 * A response for a {@link GetFollowingCountRequest}
 */
public class GetFollowersCountResponse extends Response {

    private int numFollowers;

    /**
     * Creates a response indicating the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful
     */
    public GetFollowersCountResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param numFollowers the number of followees included in the result.
     */
    public GetFollowersCountResponse(int numFollowers) {
        super(true);
        this.numFollowers = numFollowers;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetFollowersCountResponse that = (GetFollowersCountResponse) o;
        return numFollowers == that.numFollowers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numFollowers);
    }
}
