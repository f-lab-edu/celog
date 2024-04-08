package dev.sijunyang.celog.core.global.error.nextVer;

import dev.sijunyang.celog.core.global.error.TitleProvider;

/**
 * 권한이 부족해 요청을 처리할 수 없는 경우 던져집니다.
 *
 * @author Sijun Yang
 */
public class InsufficientPermissionException extends RuntimeException implements TitleProvider {

    public InsufficientPermissionException(String message) {
        super(message);
    }

    public InsufficientPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getTitle() {
        return "Insufficient_Permission";
    }

}
