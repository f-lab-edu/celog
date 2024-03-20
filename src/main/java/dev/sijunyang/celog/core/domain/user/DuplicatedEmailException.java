package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.error.ConflictException;

public class DuplicatedEmailException extends ConflictException {

    public DuplicatedEmailException(String email) {
        super(getMessage(email));
    }

    public DuplicatedEmailException(String email, Throwable cause) {
        super(getMessage(email), cause);
    }

    public DuplicatedEmailException(String email, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(getMessage(email), cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getTitle() {
        return "Duplicated_Email";
    }

    private static String getMessage(String email) {
        return "Duplicated email. input: " + email;
    }

}
