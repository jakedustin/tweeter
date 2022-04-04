package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IFeedDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class FeedDynamoDAO implements IFeedDAO {

    @Override
    public Pair<List<StatusDTO>, Boolean> getFeed(String userAlias, int limit, StatusDTO lastStatus) throws Exception {
        System.out.println("FeedDynamoDAO : getFeed : attempting to get feed for " + userAlias);
        // need to paginate results

        NameMap nameMap = new NameMap().with("#alias", "user-alias");
        ValueMap valueMap = new ValueMap().with(":user", userAlias);
        QuerySpec querySpec = new QuerySpec()
                .withNameMap(nameMap)
                .withValueMap(valueMap)
                .withKeyConditionExpression("#alias = :user")
                .withScanIndexForward(true)
                .withMaxResultSize(limit);

        if (lastStatus != null) {
                querySpec = querySpec.withExclusiveStartKey(
                    "user-alias",
                    lastStatus.getUserAlias(),
                    "datetime",
                    lastStatus.getDatetime());
        }

        ArrayList<StatusDTO> feed = new ArrayList<>();
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;
        boolean hasMorePages = true;

        try {
            items = DynamoDBHelper.getInstance().getFeedTable().query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                feed.add(new StatusDTO(
                        item.get("post").toString(),
                        item.get("datetime").toString(),
                        item.get("author-alias").toString(),
                        (List<String>) item.get("urls"),
                        (List<String>) item.get("mentions")));
            }
            hasMorePages = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() != null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("FeedDynamoDAO : getFeed : successfully retrieved feed for " + userAlias);
        return new Pair<>(feed, hasMorePages);
    }

    @Override
    public void postStatusToFeed(StatusDTO status, String userAlias) throws Exception {
        PutItemOutcome outcome = DynamoDBHelper.getInstance().getFeedTable()
                .putItem(
                        new Item()
                                .withPrimaryKey(
                                        "user-alias",
                                        userAlias,
                                        "datetime",
                                        status.getDatetime()
                                )
                                .withString("post", status.getPost())
                                .withString("author-alias", status.getUserAlias())
                                .withList("urls", status.getUrls())
                                .withList("mentions", status.getMentions())
                );
    }
}
