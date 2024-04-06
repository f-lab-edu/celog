package dev.sijunyang.celog.core.global.error.nextVer;

import dev.sijunyang.celog.core.global.error.TitleProvider;

/**
 * 특정 자원을 찾을 수 없는 경우 발생합니다.
 *
 * @author Sijun Yang
 */
public class ResourceNotFoundException extends RuntimeException implements TitleProvider {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getTitle() {
        return "Resource_Not_Found";
    }

}
