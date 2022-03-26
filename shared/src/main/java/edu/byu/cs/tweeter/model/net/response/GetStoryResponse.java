package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.Status;

import java.util.List;

public class GetStoryResponse extends PagedResponse {

    private List<Status> story;

    /**
     * Creates a response indicating the corresponding request was unsuccessful. Sets the
     * success and more pages indicators to false.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public GetStoryResponse(String message) {
        super(false, message, false);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param story the statuses to be included in the result.
     * @param hasMorePages an indicator of whether more data is available for the request.
     */
    public GetStoryResponse(List<Status> story, boolean hasMorePages) {
        super(true, hasMorePages);
        this.story = story;
    }

    public List<Status> getStory() {
        return story;
    }

    public void setStory(List<Status> story) {
        this.story = story;
    }
}
