package dev.sijunyang.celog.core.global.error;

/**
 * {@link ExpectedException}를 상속하는 추상클래스입니다. 잘못된 입력 값이 사용되었을 때 사용합니다.
 *
 * @author Sijun Yang
 * @see ExpectedException
 */
public abstract class BadRequestException extends ExpectedException {

    protected BadRequestException(String message) {
        super(message);
    }

    protected BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BadRequestException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
