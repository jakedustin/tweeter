package edu.byu.cs.tweeter.model.net.response;

public class FillResponse extends Response {
    public FillResponse(boolean success) {
        super(success);
    }

    public FillResponse(boolean success, String message) {
        super(success, message);
    }
}
