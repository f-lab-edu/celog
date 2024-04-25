package dev.sijunyang.celog.surpport.security;

import dev.sijunyang.celog.api.AuthenticatedUserManager;
import dev.sijunyang.celog.core.global.enums.Role;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * AuthenticatedUserManager 인터페이스를 구현한 클래스로, Spring Security에서 인증된 사용자 정보를 가져와서 제공합니다.
 *
 * @author Sijun Yang
 */
@Component
public class AuthenticatedUserManagerImpl implements AuthenticatedUserManager {

    @Override
    public Long getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("사용자를 식별할 수 없습니다. authentication: " + authentication);
        }

        // 인증 된 사용자의 이름 속성을 가져와 Long 타입으로 변환하여 반환합니다.
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            return Long.valueOf(oAuth2User.getName());
        }
        else {
            throw new RuntimeException("지원하지 않는 authentication 객체입니다. authentication: " + authentication);
        }
    }

    @Override
    public Role getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("사용자를 식별할 수 없습니다. authentication: " + authentication);
        }

        // Authentication 객체를 가져와 Role 객체를 가져온다.
        // 인증 된 사용자는 항상 속성 값으로 Role 객체를 가지고 있다.
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            return oAuth2User.getAttribute(CustomOauth2UserService.CELOG_ROLE);
        }
        else {
            throw new RuntimeException("지원하지 않는 authentication 객체입니다. authentication: " + authentication);
        }
    }

}
