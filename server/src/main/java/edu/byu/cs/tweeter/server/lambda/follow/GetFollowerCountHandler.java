package edu.byu.cs.tweeter.server.lambda.follow;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowerCountHandler implements RequestHandler<GetFollowersCountRequest, GetFollowersCountResponse> {
    @Override
    public GetFollowersCountResponse handleRequest(GetFollowersCountRequest request, Context context) {
        FollowService service = new FollowService(new DynamoDAOFactory());
        return service.getFollowersCount(request);
    }
}