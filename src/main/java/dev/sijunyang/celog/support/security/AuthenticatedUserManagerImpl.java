package dev.sijunyang.celog.support.security;

import dev.sijunyang.celog.api.AuthenticatedUserManager;
import dev.sijunyang.celog.core.global.enums.Role;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserManagerImpl implements AuthenticatedUserManager {

    @Override
    public Long getId() {
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(oAuth2User.getName());
    }

    @Override
    public Role getRole() {
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return oAuth2User.getAttribute("role");
    }

}
