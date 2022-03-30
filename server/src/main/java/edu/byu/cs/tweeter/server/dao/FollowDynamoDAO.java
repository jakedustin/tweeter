package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class FollowDynamoDAO implements IFollowDAO {
    private final String HASH_KEY_VAL = "follower_handle";
    private final String RANGE_KEY_VAL = "followee_handle";
    System.Logger logger = System.getLogger("FollowDynamoDAO");

    @Override
    public FollowResponse follow(String followerAlias, String followeeAlias) {
        // don't need to query to see if relationship already exists;
        // method will not be called if relationship exists
        try {
            System.out.println(HASH_KEY_VAL + ": " + followerAlias);
            System.out.println(RANGE_KEY_VAL + ": " + followeeAlias);
            PutItemOutcome outcome = DynamoDBHelper.getInstance().getFollowTable().putItem(
                    new Item().withPrimaryKey(
                            HASH_KEY_VAL, followerAlias,
                            RANGE_KEY_VAL, followeeAlias
                    )
            );
            return new FollowResponse(true);
        } catch (Exception e) {
            return new FollowResponse(false, e.getMessage());
        }
    }

    @Override
    public UnfollowResponse unfollow(String followerAlias, String followeeAlias) {
        try {
            DeleteItemOutcome outcome = DynamoDBHelper.getInstance().getFollowTable().deleteItem(
                    HASH_KEY_VAL, followerAlias,
                    RANGE_KEY_VAL, followeeAlias
            );
            return new UnfollowResponse(true);
        } catch (Exception e) {
            return new UnfollowResponse(false, e.getMessage());
        }
    }

    @Override
    public Pair<List<String>, Boolean> getFollowing(String followerHandle, int limit, String lastReturnedFollowee) {
        List<String> followees = new ArrayList<>();

        boolean hasMorePages = false;

        if (limit > 0) {
            HashMap<String, Object> valueMap = new HashMap<>();
            valueMap.put(":F", followerHandle);

            QuerySpec querySpec = new QuerySpec()
                    .withValueMap(valueMap)
                    .withKeyConditionExpression("follower_handle = :F")
                    .withScanIndexForward(true)
                    .withMaxResultSize(DynamoDBHelper.PAGE_SIZE);
            if (lastReturnedFollowee != null && !lastReturnedFollowee.equals("")) {
                System.out.println("Adding exclusive start key");
                querySpec = querySpec.withExclusiveStartKey(
                        new PrimaryKey(
                                HASH_KEY_VAL,
                                followerHandle,
                                RANGE_KEY_VAL,
                                lastReturnedFollowee
                        )
                );
                System.out.println("Added exclusive start key " + followerHandle + " : " + lastReturnedFollowee);
            }

            ItemCollection<QueryOutcome> items = DynamoDBHelper.getInstance()
                    .getFollowTable()
                    .query(querySpec);

            Iterator<Item> iterator = items.iterator();
            Item item;
            while (iterator.hasNext()) {
                System.out.println("Iterator has next");
                item = iterator.next();
                followees.add(item.getString("followee_handle"));
                System.out.println("Iterator returned the following follower_alias: " + item);
            }

            if (items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() != null) {
                hasMorePages = true;
            }
        }

        return new Pair<>(followees, hasMorePages);
    }

    @Override
    public Pair<List<String>, Boolean> getFollowers(String followeeHandle, int limit, String lastReturnedFollower) {
        List<String> followers = new ArrayList<>(limit);
        System.out.println("Attempting to get da followers");

        boolean hasMorePages = false;

        if (limit > 0) {
            HashMap<String, Object> valueMap = new HashMap<>();
            valueMap.put(":F", followeeHandle);

            System.out.println("Creating query spec");
            QuerySpec querySpec = new QuerySpec()
                    .withValueMap(valueMap)
                    .withKeyConditionExpression("followee_handle = :F")
                    .withScanIndexForward(true)
                    .withMaxResultSize(DynamoDBHelper.PAGE_SIZE);
            System.out.println("Created query spec");
            if (lastReturnedFollower != null && !lastReturnedFollower.equals("")) {
                System.out.println("Adding exclusive start key");
                querySpec = querySpec.withExclusiveStartKey(
                        new PrimaryKey(
                                RANGE_KEY_VAL,
                                followeeHandle,
                                HASH_KEY_VAL,
                                lastReturnedFollower
                        )
                );
                System.out.println("Added exclusive start key");
            }

            System.out.println("Querying for da items");
            ItemCollection<QueryOutcome> items = DynamoDBHelper.getInstance()
                    .getFollowTable()
                    .getIndex("follows_index")
                    .query(querySpec);
            System.out.println("Successfully retrieved " + items.getAccumulatedItemCount() + " items");

            System.out.println("Creating an iterator");
            Iterator<Item> iterator = items.iterator();
            Item item;
            while (iterator.hasNext()) {
                System.out.println("Iterator has next");
                item = iterator.next();
                followers.add(item.getString("follower_handle"));
                System.out.println("Iterator returned the following follower_alias: " + item);
            }

            if (items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() != null) {
                hasMorePages = true;
            }
        }

        return new Pair<>(followers, hasMorePages);
    }


    @Override
    public int getFollowingCount(String followerAlias) {
        DynamoDBHelper.getInstance().getFollowTable();

        return 0;
    }

    @Override
    public int getFollowersCount(String followeeAlias) {
        DynamoDBHelper.getInstance().getFollowTable();
        return 0;
    }

    @Override
    public boolean isFollower(String followerAlias, String followeeAlias) throws Exception {
        return DynamoDBHelper.getInstance().getFollowTable()
                .getItem(HASH_KEY_VAL, followerAlias, RANGE_KEY_VAL, followeeAlias) != null;
    }

    @Override
    public void addFollowersBatch(List<String> users, String followTarget) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(DynamoDBHelper.getInstance().getFollowTableName());

        // Add each user into the TableWriteItems object
        for (String user : users) {
            Item item = new Item()
                    .withPrimaryKey(HASH_KEY_VAL, user, RANGE_KEY_VAL, followTarget);
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(DynamoDBHelper.getInstance().getFollowTableName());
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = DynamoDBHelper.getInstance().getDB().batchWriteItem(items);
        logger.log(System.Logger.Level.DEBUG, "Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = DynamoDBHelper.getInstance().getDB().batchWriteItemUnprocessed(unprocessedItems);
            logger.log(System.Logger.Level.DEBUG, "Wrote more Users");
        }
    }
}
