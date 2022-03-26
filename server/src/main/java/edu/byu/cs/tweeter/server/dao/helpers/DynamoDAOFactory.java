package edu.byu.cs.tweeter.server.dao.helpers;

import edu.byu.cs.tweeter.server.dao.AuthTokenDynamoDAO;
import edu.byu.cs.tweeter.server.dao.FeedDynamoDAO;
import edu.byu.cs.tweeter.server.dao.FollowDynamoDAO;
import edu.byu.cs.tweeter.server.dao.RegisteredDynamoDAO;
import edu.byu.cs.tweeter.server.dao.StatusDynamoDAO;
import edu.byu.cs.tweeter.server.dao.StoryDynamoDAO;
import edu.byu.cs.tweeter.server.dao.UserDynamoDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IRegisteredUserDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;

public class DynamoDAOFactory implements IDAOFactory {
    @Override
    public IAuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDynamoDAO();
    }

    @Override
    public IRegisteredUserDAO getRegisterUserDAO() {
        return new RegisteredDynamoDAO();
    }

    @Override
    public IUserDAO getUserDAO() {
        return new UserDynamoDAO();
    }

    @Override
    public IFollowDAO getFollowDAO() {
        return new FollowDynamoDAO();
    }

    @Override
    public IFeedDAO getFeedDAO() {
        return new FeedDynamoDAO();
    }

    @Override
    public IStoryDAO getStoryDAO() {
        return new StoryDynamoDAO();
    }

    @Override
    public IStatusDAO getStatusDAO() {
        return new StatusDynamoDAO();
    }
}
