package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService {
    public interface GetStoryObserver {
        void getStorySucceeded(List<Status> statusList, Status lastStatus, boolean hasMorePages);
        void getStoryFailed(String errorMessage);
        void getStoryThrew(Exception ex);
    }

    public void getStoryItems(AuthToken authToken,
                             User targetUser,
                             int numUsersToFetch,
                             Status lastStatusFetchedPreviously,
                             StoryService.GetStoryObserver getStoryObserver) {

        GetStoryTask getStoryTask = new GetStoryTask(
                authToken,
                targetUser,
                numUsersToFetch,
                lastStatusFetchedPreviously,
                new StoryService.GetStoryHandler(getStoryObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getStoryTask);
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends Handler {
        private GetStoryObserver getStoryObserver;

        public GetStoryHandler(GetStoryObserver observer) {
            this.getStoryObserver = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {

            boolean success = msg.getData().getBoolean(GetStoryTask.SUCCESS_KEY);
            if (success) {
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.STATUSES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);

                Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;

                this.getStoryObserver.getStorySucceeded(statuses, lastStatus, hasMorePages);
            } else if (msg.getData().containsKey(GetStoryTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetStoryTask.MESSAGE_KEY);
                getStoryObserver.getStoryFailed(message);
            } else if (msg.getData().containsKey(GetStoryTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetStoryTask.EXCEPTION_KEY);
                this.getStoryObserver.getStoryThrew(ex);
                ex.printStackTrace();
            }
        }
    }
}
