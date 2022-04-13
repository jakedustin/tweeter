package edu.byu.cs.tweeter.server.lambda.status;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.PostStatusService;

public class QueuedStatusHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println("Retrieving request.");
            StatusDTO status = new Gson().fromJson(msg.getBody(), StatusDTO.class);
            System.out.println("Request retrieved.");
            System.out.println("Request data: ");
            System.out.println("\t\tPoster alias: " + status.getUserAlias());
            System.out.println("\t\tPost text: " + status.getPost());
            PostStatusService service = new PostStatusService(new DynamoDAOFactory());
            System.out.println("Attempting to batch and queue statuses.");
            service.batchAndQueueStatuses(status);
        }

        return null;
    }
}
