package edu.byu.cs.tweeter.server.lambda.user;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterUserResponse;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that submits a request to register a new user.
 */
public class RegisterUserHandler implements RequestHandler<RegisterUserRequest, RegisterUserResponse> {

    @Override
    public RegisterUserResponse handleRequest(RegisterUserRequest request, Context context) {
        UserService service = new UserService(new DynamoDAOFactory());
        return service.registerUser(request);
    }
}
