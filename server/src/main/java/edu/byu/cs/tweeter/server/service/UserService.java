package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterUserResponse;
import edu.byu.cs.tweeter.server.dao.exceptions.InvalidCredentialsException;
import edu.byu.cs.tweeter.server.dao.helpers.SecurityHelper;
import edu.byu.cs.tweeter.server.dao.interfaces.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IRegisteredUserDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;

public class UserService {
    private final IRegisteredUserDAO registeredUserDAO;
    private final IAuthTokenDAO authTokenDAO;
    private final IUserDAO userDAO;

    public UserService(IDAOFactory factory) {
        this.registeredUserDAO = factory.getRegisterUserDAO();
        this.authTokenDAO = factory.getAuthTokenDAO();
        this.userDAO = factory.getUserDAO();
    }

    public LoginResponse login(LoginRequest request) {
        LoginResponse response;
        try {
            if (SecurityHelper.validatePassword(request.getPassword(),
                    registeredUserDAO.getHashedPassword(request))) {
                response = new LoginResponse(
                        userDAO.getUser(request.getUsername()),
                        authTokenDAO.generateNewToken(request.getUsername()));
            } else {
                throw new InvalidCredentialsException("Invalid password.");
            }
        } catch (Exception e) {
            response = new LoginResponse(e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        System.out.println("Beginning RegisterUserService...");
        RegisterUserResponse response;
        try {
            System.out.println("Setting secure password");
            request.setPassword(SecurityHelper.generateStrongPasswordHash(request.getPassword()));
            System.out.println("Secure password set: " + request.getPassword());

            System.out.println("Attempting to register user...");
            response = registeredUserDAO.registerUser(request);
            System.out.println("Register user succeeded: " + response.getUser());

            System.out.println("Attempting to generate auth token...");
            response.setAuthToken(authTokenDAO.generateNewToken(request.getUsername()));
            System.out.println("Auth token generation succeeded: " + response.getAuthToken().getToken());

            System.out.println("Attempting to post new user object...");
            response.setUser(userDAO.postNewUser(request));
            System.out.println("New user creation succeeded");
        } catch (Exception e) {
            response = new RegisterUserResponse(false, e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public GetUserResponse getUser(GetUserRequest request) {
        System.out.println("UserService : getUser : Beginning GetUserRequest...");
        GetUserResponse response;
        try {
            System.out.println("UserService : getUser : verifying auth token");
            authTokenDAO.verifyAuthToken(request.getAuthToken());
            System.out.println("UserService : getUser : verifying auth token");

            System.out.println("UserService : getUser : getting user from db");
            response = new GetUserResponse(userDAO.getUser(request.getAlias()));
            System.out.println("UserService : getUser : got user " + response.getUser().getAlias() + " successfully");
        } catch (Exception e) {
            System.out.println("UserService : getUser : get user request failed: " + e.getMessage());
            e.printStackTrace();
            response = new GetUserResponse(false, e.getMessage());
            return response;
        }

        System.out.println("UserService : getUser : request succeeded");
        return response;
    }
}
