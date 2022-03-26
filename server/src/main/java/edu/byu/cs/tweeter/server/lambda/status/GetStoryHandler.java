package edu.byu.cs.tweeter.server.lambda.status;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.service.GetStoryService;

import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;

public class GetStoryHandler implements RequestHandler<GetStoryRequest, GetStoryResponse> {

    @Override
    public GetStoryResponse handleRequest(GetStoryRequest request, Context context) {
        IDAOFactory factory = new DynamoDAOFactory();
        GetStoryService service = new GetStoryService(factory);
        return service.getStory(request);
    }
}
