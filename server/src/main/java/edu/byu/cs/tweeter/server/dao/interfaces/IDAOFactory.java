package edu.byu.cs.tweeter.server.dao.interfaces;

public interface IDAOFactory {
    IAuthTokenDAO getAuthTokenDAO();
    IRegisteredUserDAO getRegisterUserDAO();
    IUserDAO getUserDAO();
    IFollowDAO getFollowDAO();
    IFeedDAO getFeedDAO();
    IStoryDAO getStoryDAO();
    IStatusDAO getStatusDAO();
}
