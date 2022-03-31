package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IFeedDAO;

public class FeedDynamoDAO implements IFeedDAO {

    @Override
    public List<StatusDTO> getFeed(String userAlias, int limit, StatusDTO lastStatus) throws Exception {
        System.out.println("FeedDynamoDAO : getFeed : attempting to get feed for " + userAlias);
        // need to paginate results
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("user-alias = " + userAlias)
                .withScanIndexForward(true)
                .withMaxResultSize(limit)
                .withExclusiveStartKey(
                        "user-alias",
                        lastStatus.getUserAlias(),
                        "datetime",
                        lastStatus.getDatetime()
                );

        ArrayList<StatusDTO> feed = new ArrayList<>();
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        try {
            items = DynamoDBHelper.getInstance().getFeedTable().query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                feed.add(new StatusDTO(
                        item.get("post").toString(),
                        item.get("user-alias").toString(),
                        item.get("dt").toString(),
                        (List<String>) item.get("urls"),
                        (List<String>) item.get("mentions")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("FeedDynamoDAO : getFeed : successfully retrieved feed for " + userAlias);
        return feed;
    }

    @Override
    public void postStatusToFeed(StatusDTO status, String userAlias) throws Exception {

    }
}
