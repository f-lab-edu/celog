package dev.sijunyang.celog.core.global.enums;

import dev.sijunyang.celog.core.domain.user.OAuth2Provider;
import dev.sijunyang.celog.core.domain.user.UserEntity;
import lombok.Getter;

/**
 * {@link UserEntity}의 인증 유형을 나타내는 Enum 클래스입니다.
 *
 * @author Sijun Yang
 */
@Getter
public enum AuthenticationType {

    /**
     * 이메일 인증 유형입니다.
     */
    EMAIL(null),

    /**
     * GitHub OAuth 인증 유형입니다.
     */
    OAUTH_GITHUB(OAuth2Provider.GITHUB),

    /**
     * Google OAuth 인증 유형입니다.
     */
    OAUTH_GOOGLE(OAuth2Provider.GOOGLE);

    private final String oauth2ProviderName;

    AuthenticationType(String oauth2ProviderName) {
        this.oauth2ProviderName = oauth2ProviderName;
    }

}
