package dev.sijunyang.celog.surpport.security;

import dev.sijunyang.celog.core.global.enums.AuthenticationType;

public interface OAuthUserInfo {

    String getEmail();

    String getName();

    String getProfileUrl();

    AuthenticationType getAuthenticationType();

}
