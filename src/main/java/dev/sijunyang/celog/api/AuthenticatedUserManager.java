package dev.sijunyang.celog.api;

import dev.sijunyang.celog.core.global.enums.Role;

/**
 * 인증된 사용자의 ID와 역할(Role)을 제공하는 인터페이스입니다.
 *
 * @author Sijun Yang
 */
public interface AuthenticatedUserManager {

    /**
     * 인증된 사용자의 ID를 반환합니다.
     * @return 사용자 ID
     */
    Long getId();

    /**
     * 인증된 사용자의 역할(Role)을 반환합니다.
     * @return 사용자 역할
     */
    Role getRole();

}
