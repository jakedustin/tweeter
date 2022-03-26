package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.LogoutDAO;

import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public class LogoutService {

    public LogoutResponse logout(LogoutRequest request) {
        return getLogoutDAO().logout(request);
    }

    private LogoutDAO getLogoutDAO() {
        return new LogoutDAO();
    }
}
