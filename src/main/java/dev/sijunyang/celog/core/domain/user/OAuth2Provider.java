package dev.sijunyang.celog.core.domain.user;

/**
 * OAuth2 제공자 서비스의 이름을 관리하는 클래스입니다. OAuth2에서 반환받는 제공자 서비스의 이름 Token 값과 동일한 문자열을 가지고 있습니다.
 *
 * @author Sijun Yang
 */
public final class OAuth2Provider {

    /**
     * Github 서비스의 이름입니다.
     */
    public static final String GITHUB = "github";

    /**
     * Google 서비스의 이름입니다.
     */
    public static final String GOOGLE = "google";

    private OAuth2Provider() {
        // 인스턴스 생성을 방지하기 위한 private 생성자
    }

}
