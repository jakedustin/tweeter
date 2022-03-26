package edu.byu.cs.tweeter.model.net.request;

public class FillRequest {
    int numUsersToGenerate;
    String followTarget;

    public FillRequest() {}

    public FillRequest(int numUsersToGenerate, String followTarget) {
        this.numUsersToGenerate = numUsersToGenerate;
        this.followTarget = followTarget;
    }

    public int getNumUsersToGenerate() {
        return numUsersToGenerate;
    }

    public void setNumUsersToGenerate(int numUsersToGenerate) {
        this.numUsersToGenerate = numUsersToGenerate;
    }

    public String getFollowTarget() {
        return followTarget;
    }

    public void setFollowTarget(String followTarget) {
        this.followTarget = followTarget;
    }
}
