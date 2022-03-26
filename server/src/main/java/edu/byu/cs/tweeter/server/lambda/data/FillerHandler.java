package edu.byu.cs.tweeter.server.lambda.data;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FillRequest;
import edu.byu.cs.tweeter.model.net.response.FillResponse;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.util.Filler;

public class FillerHandler implements RequestHandler<FillRequest, FillResponse> {
    @Override
    public FillResponse handleRequest(FillRequest request, Context context) {
        Filler filler = new Filler(request, new DynamoDAOFactory());
        return filler.fillDatabase(request);
    }
}
