package dev.sijunyang.celog.core.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OauthUserTest {

    // VO로서 동작하는지 테스트
    @Test
    void testEqualsAndHashCode() {
        OauthUser first = new OauthUser(new String("google"), new String("12345"));
        OauthUser second = new OauthUser(new String("google"), new String("12345"));

        // 각 객체/모든 필드가 서로 다른 메모리 위치에 정의되었는가?
        assertNotEquals(System.identityHashCode(first), System.identityHashCode(second));
        assertNotEquals(System.identityHashCode(first.getOauthUserId()),
                System.identityHashCode(second.getOauthUserId()));
        assertNotEquals(System.identityHashCode(first.getOauthProviderName()),
                System.identityHashCode(second.getOauthProviderName()));

        // 두 객체는 동일한가?
        assertEquals(first, second);
    }

}
