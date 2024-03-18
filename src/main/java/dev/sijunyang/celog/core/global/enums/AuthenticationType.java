package dev.sijunyang.celog.core.global.enums;

import dev.sijunyang.celog.core.domain.user.UserEntity;

/**
 * {@link UserEntity}의 인증 유형을 나타내는 Enum 클래스입니다.
 *
 * @author Sijun Yang
 */
public enum AuthenticationType {

    /**
     * 이메일 인증 유형입니다.
     */
    EMAIL,

    /**
     * GitHub OAuth 인증 유형입니다.
     */
    OAUTH_GITHUB,

    /**
     * Google OAuth 인증 유형입니다.
     */
    OAUTH_GOOGLE;

}
