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
        try {
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            // post status to story
            storyDAO.postStatusToStory(request.getStatus());

            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult result = sqs.sendMessage(createStatusQueueRequest(request.getStatus()));

            return new PostStatusResponse(true);
        } catch (Exception ex) {
            return new PostStatusResponse(false, ex.getMessage());
        }
    }

    public void batchAndQueueStatuses(PostStatusRequest request) {
        // now the hard part
        // create a request for posting the status to feeds
        // will get the followers in the next handler

        try {
            List<String> followerAliases = followDAO.getAllFollowerAliases(request.getStatus().getUserAlias());
            List<String> batchedAliases = new ArrayList<>();
            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            // need to create jobs for each group of 25 followers
            for (String user : followerAliases) {
                if (batchedAliases.size() != 25) {
                    batchedAliases.add(user);
                } else {
                    SendMessageResult result = sqs.sendMessage(new SendMessageRequest()
                            .withQueueUrl(feedQueueUrl)
                            .withMessageBody(
                                    new Gson()
                                            .toJson(
                                                    new PostStatusToFeedRequest(
                                                            batchedAliases,
                                                            request.getStatus()
                                                    ),
                                                    PostStatusToFeedRequest.class
                                            )
                            )
                    );
                    batchedAliases.clear();
                }
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

    private SendMessageRequest createStatusQueueRequest(StatusDTO status) {
        return new SendMessageRequest()
                .withQueueUrl(statusQueueUrl)
                .withMessageBody(new Gson().toJson(status, StatusDTO.class))
                .withDelaySeconds(5);
    }
}
