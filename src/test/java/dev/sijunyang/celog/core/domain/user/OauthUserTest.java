package dev.sijunyang.celog.core.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OauthUserTest {

    @Test
    void Test_EqualsAndHashCode() {
        OauthUser first = new OauthUser(new String("google"), new String("12345"));
        OauthUser second = new OauthUser(new String("google"), new String("12345"));

        assertNotEquals(System.identityHashCode(first), System.identityHashCode(second));
        assertNotEquals(System.identityHashCode(first.getOauthUserId()),
                System.identityHashCode(second.getOauthUserId()));
        assertNotEquals(System.identityHashCode(first.getOauthProviderName()),
                System.identityHashCode(second.getOauthProviderName()));
        assertEquals(first, second);
        assertEquals(first, second);
    }

}
