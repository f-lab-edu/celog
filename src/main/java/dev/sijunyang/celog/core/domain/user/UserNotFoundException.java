package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.error.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    protected UserNotFoundException(Long userId) {
        super(getMessage(userId));
    }

    protected UserNotFoundException(Long userId, Throwable cause) {
        super(getMessage(userId), cause);
    }

    protected UserNotFoundException(Long userId, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(getMessage(userId), cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getTitle() {
        return "User_Not_Found";
    }

    private static String getMessage(Long userId) {
        return "User not found. input: " + userId;
    }

}
