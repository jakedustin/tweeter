package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;

public class GetFeedService {
    private IFeedDAO feedDAO;
    private IUserDAO userDAO;

    public GetFeedService(IDAOFactory factory) {
        feedDAO = factory.getFeedDAO();
        userDAO = factory.getUserDAO();
    }

    public GetFeedResponse getFeed(GetFeedRequest request) {
        try {
            List<StatusDTO> feed = feedDAO.getFeed(request.getUserAlias(), request.getLimit(), request.getLastStatus());
            List<Status> feedWithUsers = new ArrayList<>();
            Map<String, User> usersByAlias = new HashMap<>();

            for (StatusDTO status : feed) {
                if (!usersByAlias.containsKey(status.getUserAlias())) {
                    usersByAlias.put(status.getUserAlias(), userDAO.getUser(status.getUserAlias()));
                }

                feedWithUsers.add(
                        new Status(
                                status.getPost(),
                                usersByAlias.get(
                                        status.getUserAlias()
                                ),
                                status.getDatetime(),
                                status.getUrls(),
                                status.getMentions()
                        )
                );
            }
            return new GetFeedResponse(
                    true,
                    "Successfully retrieved users",
                    feedWithUsers,
                    true
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
