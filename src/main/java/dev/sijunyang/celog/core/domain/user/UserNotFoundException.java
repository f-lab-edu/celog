package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.error.NotFoundException;

import java.util.Map;

public class UserNotFoundException extends NotFoundException {

    protected UserNotFoundException(Map<String, Object> inputs) {
        super(getMessage(inputs));
    }

    protected UserNotFoundException(Map<String, Object> inputs, Throwable cause) {
        super(getMessage(inputs), cause);
    }

    protected UserNotFoundException(Map<String, Object> inputs, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(getMessage(inputs), cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getTitle() {
        return "User_Not_Found";
    }

    private static String getMessage(Map<String, Object> inputs) {
        return "User not found. inputs: " + inputs;
    }

}
