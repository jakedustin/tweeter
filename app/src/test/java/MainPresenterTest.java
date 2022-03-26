import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterTest {

    private MainPresenter.View mockMainView;
    private StatusService mockStatusService;

    private MainPresenter mainPresenterSpy;

    @Before
    public void setup() {
        mockMainView = Mockito.mock(MainPresenter.View.class);
        mockStatusService = Mockito.mock(StatusService.class);

        User user = new User();

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView, user, true));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
    }

    @Test
    public void testPostStatus_postSucceeds() {
        Answer<Void> postSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.StatusObserver observer = (StatusService.StatusObserver) invocation.getArguments()[1];
                observer.postStatusSucceeded();
                return null;
            }
        };

        Mockito.doAnswer(postSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(Status.class), Mockito.any(StatusService.StatusObserver.class));
        mainPresenterSpy.postStatus(Mockito.any());

        // View hides its logging out message
        Mockito.verify(mockMainView).displayMessage("Posting Status...");
        Mockito.verify(mockStatusService).postStatus(Mockito.any(Status.class), Mockito.any(StatusService.StatusObserver.class));
        Mockito.verify(mockMainView).clearMessage();
        Mockito.verify(mockMainView).displayMessage("Successfully posted status");
    }

    @Test
    public void testPostStatus_postSucceedsWithCorrectParams() {

        Answer<Void> postSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertEquals("hello world", ((Status) invocation.getArguments()[0]).post);
                Assert.assertEquals(mainPresenterSpy, invocation.getArguments()[1]);
                StatusService.StatusObserver observer = (StatusService.StatusObserver) invocation.getArguments()[1];
                observer.postStatusSucceeded();
                return null;
            }
        };

        Mockito.doAnswer(postSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(Status.class), Mockito.any(StatusService.StatusObserver.class));
        mainPresenterSpy.postStatus("hello world");

        // View hides its logging out message
        Mockito.verify(mockMainView).displayMessage("Posting Status...");
        Mockito.verify(mockStatusService).postStatus(Mockito.any(Status.class), Mockito.any(StatusService.StatusObserver.class));
        Mockito.verify(mockMainView).clearMessage();
        Mockito.verify(mockMainView).displayMessage("Successfully posted status");
    }

    @Test
    public void testPostStatus_postFails() {
        Answer<Void> postSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.StatusObserver observer = (StatusService.StatusObserver) invocation.getArguments()[1];
                observer.postStatusFailed("error message");
                return null;
            }
        };

        Mockito.doAnswer(postSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(Status.class), Mockito.any(StatusService.StatusObserver.class));
        mainPresenterSpy.postStatus(Mockito.any());

        // View hides its logging out message
        Mockito.verify(mockMainView).displayMessage("Posting Status...");
        Mockito.verify(mockStatusService).postStatus(Mockito.any(Status.class), Mockito.any(StatusService.StatusObserver.class));
        Mockito.verify(mockMainView).clearMessage();
        Mockito.verify(mockMainView).displayMessage("error message");
    }

    @Test
    public void testPostStatus_postThrowsException() {
        Answer<Void> postSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.StatusObserver observer = (StatusService.StatusObserver) invocation.getArguments()[1];
                observer.postStatusThrewException(new Exception("Test exception"));
                return null;
            }
        };

        Mockito.doAnswer(postSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(Status.class), Mockito.any(StatusService.StatusObserver.class));
        mainPresenterSpy.postStatus(Mockito.any());

        // View hides its logging out message
        Mockito.verify(mockMainView).displayMessage("Posting Status...");
        Mockito.verify(mockStatusService).postStatus(Mockito.any(Status.class), Mockito.any(StatusService.StatusObserver.class));
        Mockito.verify(mockMainView).clearMessage();
        Mockito.verify(mockMainView).displayMessage("Test exception");
    }
}
