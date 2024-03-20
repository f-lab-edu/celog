package dev.sijunyang.celog.core.global.error;

/**
 * {@link ExpectedException}를 상속하는 추상클래스입니다. 행동의 주체가 요청을 처리할 수 있는 권한이 없는 경우 사용합니다.
 *
 * @author Sijun Yang
 * @see ExpectedException
 */
public abstract class ForbiddenException extends ExpectedException {

    protected ForbiddenException(String message) {
        super(message);
    }

    protected ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ForbiddenException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
