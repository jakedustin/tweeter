package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IFeedDAO;

public class FeedDynamoDAO implements IFeedDAO {

    @Override
    public GetFeedResponse getFeed(GetFeedRequest request) throws Exception {
        System.out.println("FeedDynamoDAO : getFeed : attempting to get feed for " + request.getTargetUser().getAlias());
        // need to paginate results
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("user-alias = " + request.getTargetUser().getAlias())
                .withScanIndexForward(true)
                .withMaxResultSize(request.getLimit())
                .withExclusiveStartKey("last-status", request.getLastStatus().getUser().getAlias());

        ArrayList<Status> feed = new ArrayList<>();
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        try {
            items = DynamoDBHelper.getInstance().getFeedTable().query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                feed.add(new Status(
                        item.get("post").toString(),
                        new User(
                                item.get("user.first-name").toString(),
                                item.get("user.last-name").toString(),
                                item.get("user.username").toString(),
                                item.get("user.image-url").toString()),
                        item.get("dt").toString(),
                        (List<String>) item.get("urls"),
                        (List<String>) item.get("mentions")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("FeedDynamoDAO : getFeed : successfully retrieved feed for " + request.getTargetUser().getAlias());
        GetFeedResponse response = new GetFeedResponse(true, false);
        response.setStatusList(feed);
        return response;
    }
}
