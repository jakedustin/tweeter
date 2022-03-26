package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthTokenDAO {
    AuthToken generateNewToken(String userAlias) throws Exception;
    void verifyAuthToken(AuthToken authToken) throws Exception;
}
