package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IStoryDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class StoryDynamoDAO implements IStoryDAO {
    @Override
    public Pair<List<StatusDTO>, Boolean> getStory(String userAlias, int limit, StatusDTO lastStatus) throws Exception {
        System.out.println("StoryDynamoDAO : getStory : attempting to get story for " + userAlias);
        // need to paginate results
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("user-alias = " + userAlias)
                .withScanIndexForward(true)
                .withMaxResultSize(limit);

        if (lastStatus != null) {
            querySpec = querySpec.withExclusiveStartKey(
                    "user-alias",
                    lastStatus.getUserAlias(),
                    "timestamp",
                    lastStatus.getDatetime()
            );
        }

        ArrayList<StatusDTO> story = new ArrayList<>();
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;
        boolean hasMorePages = true;

        try {
            items = DynamoDBHelper.getInstance().getStoryTable().query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                story.add(new StatusDTO(
                        item.get("post").toString(),
                        item.get("user-alias").toString(),
                        item.get("timestamp").toString(),
                        (List<String>) item.get("urls"),
                        (List<String>) item.get("mentions")));
            }
            hasMorePages = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() != null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("FeedDynamoDAO : getFeed : successfully retrieved story for " + userAlias);
        return new Pair<>(story, hasMorePages);
    }

    @Override
    public void postStatusToStory(StatusDTO status) throws Exception {
        PutItemOutcome outcome = DynamoDBHelper.getInstance().getStoryTable()
                .putItem(
                        new Item()
                            .withPrimaryKey(
                                    "user-alias",
                                    status.getUserAlias(),
                                    "timestamp",
                                    status.getDatetime()
                            )
                        .withString("post", status.getPost())
                        .withList("urls", status.getUrls())
                        .withList("mentions", status.getMentions())
                );
    }
}
