package dev.sijunyang.celog.core.domain.user;

import java.util.Map;

import dev.sijunyang.celog.core.global.error.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Map<String, Object> inputs) {
        super(getMessage(inputs));
    }

    public UserNotFoundException(Map<String, Object> inputs, Throwable cause) {
        super(getMessage(inputs), cause);
    }

    public UserNotFoundException(Map<String, Object> inputs, Throwable cause, boolean enableSuppression,
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
