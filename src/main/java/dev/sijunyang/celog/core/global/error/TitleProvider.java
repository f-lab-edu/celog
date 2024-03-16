package dev.sijunyang.celog.core.global.error;

/**
 * 예외 발생 시 예외 타입을 식별할 수 있는 고유한 문자열을 반환하는 역할의 인터페이스입니다.
 *
 * @author Sijun Yang
 */
interface TitleProvider {

    /**
     * 예외 타입을 식별할 수 있는 고유한 문자열을 반환합니다.
     * @return 예외 타입을 식별할 수 있는 고유한 문자열
     */
    String getTitle();

}
