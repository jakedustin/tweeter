package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService {

    public interface GetFeedObserver {
        void getFeedSucceeded(List<Status> statusList, Status lastStatus, boolean hasMorePages);
        void getFeedFailed(String errorMessage);
        void getFeedThrew(Exception ex);
    }

    /**
     * <p>liveStatuses := the list of statuses currently available in the view</p>
     * <p>newStatuses := the list of statuses retrieved by getFeed() method</p>
     * <p>sizeOf(x) := the number of distinct objects contained in x</p>
     *
     * <p>requires inputs are not null</p>
     * <p>requires valid authToken</p>
     *
     * <p>ensures sizeOf(liveStatuses \cap newStatuses) == 0</p>
     * <p>ensures sizeOf(liveStatuses \cup newStatuses) == sizeOf(liveStatuses) + sizeOf(newStatuses)</p>
     *
     * @param authToken the authToken of the user logged in to the app
     * @param targetUser the user for whom we are fetching a feed
     * @param numUsersToFetch the max number of users to return
     * @param lastStatusFetchedPreviously the final status returned last time
     * @param getFeedObserver the object implementing the observer interface that will
     *                             action the message provided
     */
    public void getFeedItems(AuthToken authToken,
                             User targetUser,
                             int numUsersToFetch,
                             Status lastStatusFetchedPreviously,
                             FeedService.GetFeedObserver getFeedObserver) {

        GetFeedTask getFeedTask = new GetFeedTask(
                authToken,
                targetUser,
                numUsersToFetch,
                lastStatusFetchedPreviously,
                new GetFeedHandler(getFeedObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends Handler {
        GetFeedObserver observer;
        public GetFeedHandler(GetFeedObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            boolean success = msg.getData().getBoolean(GetFeedTask.SUCCESS_KEY);
            if (success) {
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.STATUSES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);

                Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;

                observer.getFeedSucceeded(statuses, lastStatus, hasMorePages);
            } else if (msg.getData().containsKey(GetFeedTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFeedTask.MESSAGE_KEY);
                observer.getFeedFailed(message);
            } else if (msg.getData().containsKey(GetFeedTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFeedTask.EXCEPTION_KEY);
                observer.getFeedThrew(ex);
            }
        }
    }
}
