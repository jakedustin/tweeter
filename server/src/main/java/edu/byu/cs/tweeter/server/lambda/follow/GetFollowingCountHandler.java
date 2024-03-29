package edu.byu.cs.tweeter.server.lambda.follow;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingCountHandler implements RequestHandler<GetFollowingCountRequest, GetFollowingCountResponse> {
    @Override
    public GetFollowingCountResponse handleRequest(GetFollowingCountRequest request, Context context) {
        FollowService service = new FollowService(new DynamoDAOFactory());
        return service.getFollowingCount(request);
    }
}
