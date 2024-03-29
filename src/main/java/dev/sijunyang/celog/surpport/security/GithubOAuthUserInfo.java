package dev.sijunyang.celog.surpport.security;

import org.springframework.security.oauth2.core.user.OAuth2User;

public class GithubOAuthUserInfo implements OAuthUserInfo {

    private final OAuth2User oAuth2User;

    public GithubOAuthUserInfo(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    @Override
    public String getEmail() {
        return this.oAuth2User.getAttribute("email");
    }

    @Override
    public String getName() {
        return this.oAuth2User.getAttribute("name");
    }

    @Override
    public String getProfileUrl() {
        return this.oAuth2User.getAttribute("avatar_url");
    }

}
