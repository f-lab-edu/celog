package dev.sijunyang.celog.core.domain.user;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자의 OAuth 관련 정보를 저장하는 클래스입니다.
 *
 * @author Sijun Yang
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class OauthUser {

    /**
     * OAuth 제공자의 이름입니다.
     */
    @NotNull
    private String oauthProviderName;

    /**
     * OAuth 제공자로부터 받은 사용자 ID입니다.
     */
    @NotNull
    private String oauthUserId;

    public OauthUser(String oauthProviderName, String oauthUserId) {
        this.oauthProviderName = oauthProviderName;
        this.oauthUserId = oauthUserId;
    }

}
