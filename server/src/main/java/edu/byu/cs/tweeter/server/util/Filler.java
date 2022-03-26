package edu.byu.cs.tweeter.server.util;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.dto.UserDTO;
import edu.byu.cs.tweeter.model.net.request.FillRequest;
import edu.byu.cs.tweeter.model.net.response.FillResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.IUserDAO;

public class Filler {
    private final IDAOFactory factory;

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final int NUM_USERS;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final String FOLLOW_TARGET;

    public Filler(FillRequest request, IDAOFactory factory) {
        this.factory = factory;
        this.FOLLOW_TARGET = request.getFollowTarget();
        this.NUM_USERS = request.getNumUsersToGenerate();
    }

    public FillResponse fillDatabase(FillRequest request) {

        // Get instance of DAOs by way of the Abstract Factory Pattern
        IUserDAO userDAO = factory.getUserDAO();
        IFollowDAO followDAO = factory.getFollowDAO();

        List<String> followers = new ArrayList<>();
        List<UserDTO> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String firstName = "Guy " + i;
            String lastName = Integer.toString(i);
            String userAlias = "@guy" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            UserDTO user = new UserDTO();
            user.setUserAlias(userAlias);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(userAlias);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (followers.size() > 0) {
            followDAO.addFollowersBatch(followers, FOLLOW_TARGET);
        }

        return new FillResponse(true, "Successfully added " + NUM_USERS + " users to the database.");
    }
}
