package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IStatusDAO;

public class StatusDynamoDAO implements IStatusDAO {
    @Override
    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            System.out.println("Attempting to query items");
            QuerySpec querySpec = new QuerySpec();
            ItemCollection<QueryOutcome> items = DynamoDBHelper.getInstance().getStatusTable().query(querySpec);
            System.out.println("Query successful");

            System.out.println("Attempting to generate an ID for the status");
            // TODO: find a way to get a real-time count of items in the database
            int statusId = (int) System.currentTimeMillis();
            System.out.println("statusId: " + statusId);

            System.out.println("Attempting to place item in db");
            PutItemOutcome putStatus = DynamoDBHelper.getInstance().getStatusTable()
                    .putItem(
                            new Item()
                                    .withPrimaryKey(
                                            "status-id", statusId,
                                            "user-alias", request.getStatus().getUser().getAlias())
                                    .withString("post", request.getStatus().getPost())
                                    .withString("dt", request.getStatus().datetime));
            System.out.println("Put attempt succeeded.");

            System.out.println("Attempting to create response.");
            PostStatusResponse response = new PostStatusResponse(true);
            System.out.println("Successfully created response.");

            return response;
        } catch (AmazonServiceException ase) {
            return new PostStatusResponse(false, ase.getMessage());
        }
    }
}
