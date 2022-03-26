package edu.byu.cs.tweeter.server.lambda.status;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.service.GetFeedService;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;

public class GetFeedHandler implements RequestHandler<GetFeedRequest, GetFeedResponse> {

    @Override
    public GetFeedResponse handleRequest(GetFeedRequest request, Context context) {
        IDAOFactory factory = new DynamoDAOFactory();
        GetFeedService service = new GetFeedService(factory);
        return service.getFeed(request);
    }
}
