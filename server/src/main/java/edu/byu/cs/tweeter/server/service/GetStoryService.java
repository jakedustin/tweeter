package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.dto.StatusDTO;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;
import edu.byu.cs.tweeter.server.util.Pair;

public class GetStoryService {
    IStoryDAO storyDAO;
    IUserDAO userDAO;

    public GetStoryService(IDAOFactory factory) {
        storyDAO = factory.getStoryDAO();
        userDAO = factory.getUserDAO();
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        try {
            Pair<List<StatusDTO>, Boolean> results = storyDAO.getStory(
                    request.getUserAlias(),
                    request.getLimit(),
                    request.getLastStatus()
            );
            List<StatusDTO> story = results.getFirst();
            List<Status> storyWithUserObject = new ArrayList<>();
            User storyOwner = userDAO.getUser(request.getUserAlias());

            for (StatusDTO status : story) {
                storyWithUserObject.add(
                        new Status(
                                status.getPost(),
                                storyOwner,
                                status.getDatetime(),
                                status.getUrls(),
                                status.getMentions()
                        )
                );
            }
            return new GetStoryResponse(storyWithUserObject, results.getSecond());
        } catch (Exception e) {
            e.printStackTrace();
            return new GetStoryResponse(e.getMessage());
        }
    }
}
