package dev.sijunyang.celog.core.global.error;

/**
 * {@link ExpectedException}를 상속하는 추상클래스입니다. 행동의 주체를 식별할 수 없는 경우 사용합니다.
 *
 * @author Sijun Yang
 * @see ExpectedException
 */
public abstract class UnauthenticatedException extends ExpectedException {

    protected UnauthenticatedException(String message) {
        super(message);
    }

    protected UnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    protected UnauthenticatedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
