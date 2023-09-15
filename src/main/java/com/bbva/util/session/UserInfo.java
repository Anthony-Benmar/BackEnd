package com.bbva.util.session;

import javax.servlet.http.HttpServletRequest;

import com.bbva.fga.session.UserSessionHelper;
import com.bbva.fga.session.beans.UserSession;

import java.util.logging.Logger;

public final class UserInfo {
    
    private static final Logger LOGGER = Logger.getLogger(UserInfo.class.getName());

    private static final String USER_DEFAULT = "SIDE000";

    private UserInfo(){}

    public static String getEmployeeId(HttpServletRequest request) {
        if(request == null){
            return USER_DEFAULT;
        }
        UserSession user = UserSessionHelper.getInstance().getUserSession(request, false);
        return user != null ? user.getEmployeeId() : USER_DEFAULT;
    }

}
