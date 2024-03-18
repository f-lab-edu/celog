package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.error.NotFoundException;

public class DuplicatedEmailException extends NotFoundException {

    protected DuplicatedEmailException(String email) {
        super(getMessage(email));
    }

    protected DuplicatedEmailException(String email, Throwable cause) {
        super(getMessage(email), cause);
    }

    protected DuplicatedEmailException(String email, Throwable cause, boolean enableSuppression,
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
