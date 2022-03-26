package edu.byu.cs.tweeter.server.dao.helpers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DynamoDBHelper {
    private static DynamoDBHelper instance = null;
    public static final int PAGE_SIZE = 10;
    private static final String AUTH_TOKEN_TN = "token";
    private static final String REGISTERED_USERS_TN = "registered_users";
    private static final String USERS_TN = "users";
    private static final String FEED_TN = "feed";
    private static final String FOLLOW_TN = "follows";
    private static final String STATUS_TN = "statuses";
    private static DynamoDB db = null;

    private DynamoDBHelper() {
        System.out.println("Establishing new DynamoDB connection...");
        db = new DynamoDB(
                AmazonDynamoDBClientBuilder.standard()
                        .withRegion("us-west-2")
                        .build());
        System.out.println("Successfully established connection");
    }

    public static DynamoDBHelper getInstance() {
        if (instance == null) {
            instance = new DynamoDBHelper();
        }
        return instance;
    }

    public DynamoDB getDB() {
        getInstance();
        return db;
    }

    private Table getTable(String tableName) {
        return getDB().getTable(tableName);
    }

    public Table getAuthTokenTable() {
        return getTable(AUTH_TOKEN_TN);
    }

    public Table getUsersTable() {
        return getTable(USERS_TN);
    }

    public String getUsersTableName() {
        return USERS_TN;
    }

    public Table getRegisteredUsersTable() {
        return getTable(REGISTERED_USERS_TN);
    }

    public Table getFeedTable() {
        return getTable(FEED_TN);
    }

    public Table getFollowTable() {
        return getTable(FOLLOW_TN);
    }

    public String getFollowTableName() {
        return FOLLOW_TN;
    }

    public Table getStatusTable() { return getTable(STATUS_TN); }
}
