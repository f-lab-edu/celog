package dev.sijunyang.celog.core.global.error;

/**
 * {@link ExpectedException}를 상속하는 추상클래스입니다. 비지니스 로직상 불가능하거나 모순이 발생한 경우 사용합니다.
 *
 * @author Sijun Yang
 * @see ExpectedException
 */
public abstract class ConflictException extends ExpectedException {

    protected ConflictException(String message) {
        super(message);
    }

    protected ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ConflictException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
