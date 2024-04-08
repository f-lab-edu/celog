package dev.sijunyang.celog.surpport.security;

import dev.sijunyang.celog.core.domain.user.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

    private final UserService userService;

    @Bean
    public CustomOauth2UserService customOauth2UserService() {
        return new CustomOauth2UserService(this.userService, new DefaultOAuth2UserService());
    }

}
