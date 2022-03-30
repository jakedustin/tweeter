package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.GetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IAuthTokenDAO;

public class AuthTokenDynamoDAO implements IAuthTokenDAO {
    private static final int TIMEOUT_IN_MINUTES = 30;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private static boolean disableAuthentication = true;

    public static AuthToken spoofAuthToken(AuthToken authToken, String userAlias) throws Exception {

        try {
            PutItemOutcome outcome = DynamoDBHelper.getInstance()
                    .getAuthTokenTable()
                    .putItem(new Item()
                            .withPrimaryKey("auth-token", authToken.getToken())
                            .withString("dt", authToken.getDatetime())
                            .withString("user-alias", userAlias));
            return authToken;
        } catch (Exception e) {
            throw new Exception("Failed to create auth token");
        }
    }

    @Override
    public AuthToken generateNewToken(String userAlias) throws Exception {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        AuthToken authToken = new AuthToken(base64Encoder.encodeToString(randomBytes), LocalDateTime.now().toString());

        try {
            PutItemOutcome outcome = DynamoDBHelper.getInstance()
                    .getAuthTokenTable()
                    .putItem(new Item()
                            .withPrimaryKey("auth-token", authToken.getToken())
                            .withString("dt", authToken.getDatetime())
                            .withString("user-alias", userAlias));
            return authToken;
        } catch (Exception e) {
            throw new Exception("Failed to create auth token: " + e.getMessage());
        }
    }

    @Override
    public void verifyAuthToken(AuthToken authToken) throws Exception {
        if (!disableAuthentication) {
            GetItemOutcome outcome = DynamoDBHelper.getInstance()
                    .getAuthTokenTable()
                    .getItemOutcome("auth-token", authToken.getToken());
            if (outcome.getItem() == null) {
                throw new Exception("Unable to authenticate user.");
            } else {
                checkTokenHasNotTimedOut(authToken.getDatetime());
                updateDateTimeOnToken(authToken.getToken());
            }
        }
    }

    private static void updateDateTimeOnToken(String token) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("auth-token", token)
                .withUpdateExpression("set dt = :d")
                .withValueMap(new ValueMap().withString(":d", LocalDateTime.now().toString()))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        UpdateItemOutcome outcome = DynamoDBHelper.getInstance()
                .getAuthTokenTable()
                .updateItem(updateItemSpec);
        System.out.println("Update item completed: " + outcome.getItem().toJSONPretty());
    }

    private static void checkTokenHasNotTimedOut(String retrievedDateTime) throws TimeoutException {
        LocalDateTime aLDT = LocalDateTime.parse(retrievedDateTime);
        long diff = Math.abs(ChronoUnit.MINUTES.between(aLDT, LocalDateTime.now()));
        System.out.println(diff + " minutes since auth token was created");
        if (diff > TIMEOUT_IN_MINUTES) {
            throw new TimeoutException("For security purposes, you have been logged out. Please log in again.");
        }
    }

    private static void enableAuthentication() {
        disableAuthentication = false;
    }

    private static void disableAuthentication() {
        disableAuthentication = true;
    }
}
