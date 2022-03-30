package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.GetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.lang.System.Logger;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.dto.UserDTO;
import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.helpers.S3ProfilePictureHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;

public class UserDynamoDAO implements IUserDAO {
    private final String DEFAULT_PROFILE_URL = "https://jd-cs340-profile-pictures.s3.us-west-2.amazonaws.com/%40default_profile";

    private final Logger logger = System.getLogger("UserDynamoDAO");
    @Override
    public User getUser(String username) {
//        String imageUrl = S3ProfilePictureHelper.getInstance().getPublicUrl(username);
//        System.out.println("imageUrl for " + username + ": " + imageUrl);
        GetItemOutcome getItemOutcome = DynamoDBHelper.getInstance().getUsersTable()
                .getItemOutcome("user-alias", username);
        return new User(getItemOutcome.getItem().get("first-name").toString(),
                getItemOutcome.getItem().get("last-name").toString(),
                username,
                getItemOutcome.getItem().get("image-url").toString());
    }

    // should split out adding the image and posting the user into two
    @Override
    public User postNewUser(RegisterUserRequest request) {
        System.out.println("Getting imageUrl");
        String imageUrl = S3ProfilePictureHelper.getInstance()
                .handleImageString(request.getImage(), request.getUsername());
        System.out.println("Getting imageUrl succeeded");
        System.out.println("Attempting to putuser");
        PutItemOutcome putUser = postNewUser(request.getUsername(), request.getFirstName(), request.getLastName(), imageUrl);
        System.out.println("putUser succeeded");
        return new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageUrl);
    }

    @Override
    public PutItemOutcome postNewUser(String alias, String firstName, String lastName, String imageUrl) {
        return DynamoDBHelper.getInstance().getUsersTable()
                .putItem(
                        new Item()
                                .withPrimaryKey("user-alias", alias)
                                .withString("first-name", firstName)
                                .withString("last-name", lastName)
                                .withString("image-url", imageUrl)
                                .withInt("followers", 0)
                                .withInt("following", 0)
                );
    }

    @Override
    public void deleteUsers(List<String> users) {
        try {
            for (String userAlias : users) {
                DeleteItemOutcome outcome = DynamoDBHelper.getInstance().getFollowTable().deleteItem(
                        "user-alias", userAlias
                );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addUserBatch(List<UserDTO> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(DynamoDBHelper.getInstance().getUsersTableName());

        // Add each user into the TableWriteItems object
        for (UserDTO user : users) {
            Item item = new Item()
                    .withPrimaryKey("user-alias", user.getUserAlias())
                    .withString("first-name", user.getFirstName())
                    .withString("last-name", user.getLastName())
                    .withString("image-url", DEFAULT_PROFILE_URL);
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(DynamoDBHelper.getInstance().getUsersTableName());
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
        logger.log(Logger.Level.DEBUG, "Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = DynamoDBHelper.getInstance().getDB().batchWriteItemUnprocessed(unprocessedItems);
            logger.log(Logger.Level.DEBUG, "Wrote more Users");
        }
    }

    @Override
    public void adjustFollowingValue(boolean isIncrement, String alias) {
        ValueMap valueMap = new ValueMap().withInt(":val", getIntValueOfBoolean(isIncrement));
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("user-alias", alias)
                .withUpdateExpression("set following = following + :val")
                .withValueMap(valueMap);

        DynamoDBHelper.getInstance().getUsersTable().updateItem(updateItemSpec);
    }

    @Override
    public void adjustFollowersValue(boolean isIncrement, String alias) {
        ValueMap valueMap = new ValueMap().withInt(":val", getIntValueOfBoolean(isIncrement));
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("user-alias", alias)
                .withUpdateExpression("set followers = followers + :val")
                .withValueMap(valueMap);

        DynamoDBHelper.getInstance().getUsersTable().updateItem(updateItemSpec);
    }

    @Override
    public int getNumFollowing(String alias) {
        GetItemOutcome getItemOutcome = DynamoDBHelper.getInstance().getUsersTable()
                .getItemOutcome("user-alias", alias);
        return getItemOutcome.getItem().getInt("following");
    }

    @Override
    public int getNumFollowers(String alias) {
        GetItemOutcome getItemOutcome = DynamoDBHelper.getInstance().getUsersTable()
                .getItemOutcome("user-alias", alias);
        return getItemOutcome.getItem().getInt("followers");
    }

    private int getIntValueOfBoolean(boolean isIncrement) {
        return isIncrement? 1 : -1;
    }

//    private List<User> getUsersFromListOfAliases(Collection<String> aliases, String table) {
//        // Item
//        Map<String, AttributeValue> attributeValuesByKeys = new TreeMap<>();
//
//        new KeysAndAttributes().setKeys(new Map<String, AttributeValue>);
//        BatchGetItemOutcome outcome = DynamoDBHelper.getInstance().getDB()
//                .batchGetItem(
//                        new BatchGetItemSpec()
//                                .withTableKeyAndAttributes(
//                                        new TableKeysAndAttributes("follows")
//                                                .withAttributeNames((List<String>) aliases)));
//        //  need to get users (from user table)
//        //  possible to get users using collection of keys?
//        //      BatchGetItems.builder().requestItems(Map<String, KeysAndAttributes> keysAndAttributesByTableName)
//        //      KeysAndAttributes.builder().keys(Map<String, AttributeValue> attributeValuesByKeys)
//        //      key would be follower_alias
//        //  use the keys and attributes to get a map of the keys and attributes for the users in the table
//        //  use the AttributeValue.builder().ss(Set<String> stringSet) to add a set of strings to the query
//        //  split method out so it can run exclusively on a list of user aliases
//        //      store the last follower alias in the presenter
//
//        return null;
//    }
}
