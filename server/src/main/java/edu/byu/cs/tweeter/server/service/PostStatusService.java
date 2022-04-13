package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusToFeedRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class PostStatusService {
    private final IAuthTokenDAO authTokenDAO;
    private final IFollowDAO followDAO;
    private final IUserDAO userDAO;
    private final IFeedDAO feedDAO;
    private final IStoryDAO storyDAO;
    private final String statusQueueUrl = "https://sqs.us-west-2.amazonaws.com/359982291524/statusQueue";
    private final String feedQueueUrl = "https://sqs.us-west-2.amazonaws.com/359982291524/feedQueue";

    public PostStatusService(IDAOFactory factory) {
        this.authTokenDAO = factory.getAuthTokenDAO();
        this.followDAO = factory.getFollowDAO();
        this.userDAO = factory.getUserDAO();
        this.feedDAO = factory.getFeedDAO();
        this.storyDAO = factory.getStoryDAO();
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        System.out.println("request.authToken : " + request.getAuthToken());
        System.out.println("request.status : " + request.getStatus());
        try {
            System.out.println("Verifying auth token");
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            // post status to story
            System.out.println("Posting status to story");
            storyDAO.postStatusToStory(request.getStatus());

            System.out.println("Attempting to queue status");
            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult result = sqs.sendMessage(new SendMessageRequest()
                    .withQueueUrl(statusQueueUrl)
                    .withMessageBody(new Gson().toJson(request.getStatus(), StatusDTO.class)));

            System.out.println("Status queued successfully.");
            return new PostStatusResponse(true);
        } catch (Exception ex) {
            return new PostStatusResponse(false, ex.getMessage());
        }
    }

    public void batchAndQueueStatuses(StatusDTO status) {
        System.out.println("Batching and queueing.");

        try {
            System.out.println("Getting the followers.");
            // need to get these but paginate them
            Pair<List<String>, Boolean> currentAliasList = new Pair<>(new ArrayList<>(), true);
            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            String lastFolloweeAlias = "";
            while (currentAliasList.getSecond()) {
                System.out.println("Getting follower list");
                currentAliasList = followDAO.getFollowers(status.getUserAlias(), 25, lastFolloweeAlias);
                System.out.println("Attempting to submit batch");
                SendMessageResult result = sqs.sendMessage(new SendMessageRequest()
                        .withQueueUrl(feedQueueUrl)
                        .withMessageBody(
                                new Gson()
                                        .toJson(
                                                new PostStatusToFeedRequest(
                                                        currentAliasList.getFirst(),
                                                        status
                                                ),
                                                PostStatusToFeedRequest.class
                                        )
                        )
                );
                System.out.println("Message submitted successfully: " + result.getMessageId());
                lastFolloweeAlias = currentAliasList.getFirst().get(24);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void publishBatchedStatusesToFeeds(List<String> aliases, StatusDTO status) {
        try {
            for (String alias : aliases) {
                feedDAO.postStatusToFeed(status, alias);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
