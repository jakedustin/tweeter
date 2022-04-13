package edu.byu.cs.tweeter.server.lambda.status;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.PostStatusToFeedRequest;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.PostStatusService;

public class EnqueuedBatchPostsHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            PostStatusToFeedRequest request = new Gson().fromJson(msg.getBody(), PostStatusToFeedRequest.class);
            PostStatusService service = new PostStatusService(new DynamoDAOFactory());
            service.publishBatchedStatusesToFeeds(request.getFollowers(), request.getStatus());
        }

        return null;
    }
}
