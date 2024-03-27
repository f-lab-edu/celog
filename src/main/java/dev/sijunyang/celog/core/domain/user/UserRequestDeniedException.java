package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.error.ForbiddenException;

public class UserRequestDeniedException extends ForbiddenException {

    public UserRequestDeniedException(long userId) {
        super(getMessage(userId));
    }

    public UserRequestDeniedException(long userId, Throwable cause) {
        super(getMessage(userId), cause);
    }

    public UserRequestDeniedException(long userId, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(getMessage(userId), cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getTitle() {
        return "User_Request_Denied";
    }

    private static String getMessage(long userId) {
        return "Insufficient permissions. userId: " + userId;
    }

}
