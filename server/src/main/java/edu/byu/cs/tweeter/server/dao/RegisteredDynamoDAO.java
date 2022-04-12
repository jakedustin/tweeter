package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.GetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterUserResponse;
import edu.byu.cs.tweeter.server.dao.exceptions.InvalidCredentialsException;
import edu.byu.cs.tweeter.server.dao.helpers.DynamoDBHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IRegisteredUserDAO;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisteredDynamoDAO implements IRegisteredUserDAO {
    /**
     * Submits a user's credentials to the RegisteredUser database.
     *
     * @param request
     * @return an empty RegisterUserResponse, to be populated in the {@link UserService}
     * @throws Exception
     */
    @Override
    public RegisterUserResponse registerUser(RegisterUserRequest request) throws Exception {
        try {
            PutItemOutcome putRegisteredUser=DynamoDBHelper.getInstance().getRegisteredUsersTable()
                    .putItem(
                            new Item()
                                    .withPrimaryKey("user-alias", request.getUsername())
                                    .withString("password", request.getPassword()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new Exception("Unable to register user.");
        }
        return new RegisterUserResponse(null, null);
    }

    /**
     * Checks a user's login credentials against the registered user database; if they are valid,
     * returns an empty LoginResponse object. If they are invalid, throws a new exception.
     *
     * @param request The LoginRequest object containing the user's credentials
     * @return an empty LoginResponse object
     * @throws Exception if the login request fails for any reason
     */
    @Override
    public String getHashedPassword(LoginRequest request) throws InvalidCredentialsException {
            GetItemOutcome getItemOutcome = DynamoDBHelper.getInstance().getRegisteredUsersTable()
                    .getItemOutcome("user-alias", request.getUsername());
            // need to validate the output of the GetItemOutcome request
            // if password is not valid, throw a new exception with the reason: invalid password
            // if no user is returned, throw a new exception with the reason: username not found
            // generate a new authtoken and attach it to the response
            // retrieve the user and attach it to the response
            if (getItemOutcome.getItem() == null) {
                throw new InvalidCredentialsException("Username not found");
            }
            return getItemOutcome.getItem().getString("password");
    }
}
