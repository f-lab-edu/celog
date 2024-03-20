package dev.sijunyang.celog.core.global.error;

/**
 * {@link ExpectedException}를 상속하는 추상클래스입니다. 자원을 찾을 수 없는 경우 사용합니다.
 *
 * @author Sijun Yang
 * @see ExpectedException
 */
public abstract class NotFoundException extends ExpectedException {

    protected NotFoundException(String message) {
        super(message);
    }

    protected NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    protected NotFoundException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
