package dev.sijunyang.celog.core.global.error.nextVer;

import dev.sijunyang.celog.core.global.error.TitleProvider;

/**
 * 클라이언트의 입력 값이 유효하지 않을 때 발생합니다.
 *
 * @author Sijun Yang
 */
public class InvalidInputException extends RuntimeException implements TitleProvider {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getTitle() {
        return "Invalid_Input";
    }

}
