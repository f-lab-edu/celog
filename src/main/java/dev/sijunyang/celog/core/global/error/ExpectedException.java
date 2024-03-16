package dev.sijunyang.celog.core.global.error;

/**
 * 핵심 비즈니스 로직에서 의도된 예외 상황을 처리하기 위한 추상 클래스입니다. <br/>
 * 이 클래스는 {@link TitleProvider} 인터페이스를 implement 합니다. 이 클래스를 상속받은 구체적인 예외 클래스들은 예외 발생 시
 * 제목(title)을 제공해야 합니다.
 *
 * @author Sijun Yang
 * @see TitleProvider
 */
abstract class ExpectedException extends RuntimeException implements TitleProvider {

    protected ExpectedException(String message) {
        super(message);
    }

    protected ExpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ExpectedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
