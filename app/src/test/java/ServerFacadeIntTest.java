import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterUserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterUserResponse;

public class ServerFacadeIntTest {
    ServerFacade server;

    @Before
    public void setup() {
        server = new ServerFacade();
    }

    @Test
    public void register() {
        RegisterUserRequest request = new RegisterUserRequest("Jake", "Dustin", "@jakeydus", "password1234", "image");
        try {
            RegisterUserResponse response = server.register(request, "register");
            Assert.assertTrue(response.isSuccess());
            Assert.assertEquals("this is a secret token message", response.getAuthToken().getToken());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFollowers() {
        FollowersRequest request = new FollowersRequest(new AuthToken(), "@isabel", 5, "@elliott");
        try {
            FollowersResponse response = server.getFollowers(request, "getfollowers");
            Assert.assertTrue(response.isSuccess());
            Assert.assertEquals(5, response.getFollowers().size());
            Assert.assertEquals("@elizabeth", response.getFollowers().get(0).getAlias());
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFollowerCount() {
        GetFollowersCountRequest request = new GetFollowersCountRequest(new AuthToken(), "allen");
        try {
            GetFollowersCountResponse response = server.getFollowersCount(request, "getfollowerscount");
            Assert.assertEquals(21, response.getNumFollowers());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void getStory() {
//        GetStoryTask mockGetStoryTask = Mockito.mock(GetStoryTask.class);
//        ServerFacade serverFacadeMock = Mockito.mock(ServerFacade.class);
//
//        Mockito.doAnswer()
//    }

}
