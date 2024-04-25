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
        Authentication authenticationOrNull = SecurityContextHolder.getContext().getAuthentication();
        if (authenticationOrNull == null || authenticationOrNull instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException(
                    "사용자를 식별할 수 없습니다. authentication: " + authenticationOrNull);
        }

        // 이제 null이 확실하게 아니므로 변수명에서 OrNull 제거.
        Authentication authentication = authenticationOrNull;

        // 인증 된 사용자의 이름(명)을 가져와 Long 타입으로 변환하여 반환합니다.
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
        Authentication authenticationOrNull = SecurityContextHolder.getContext().getAuthentication();
        if (authenticationOrNull == null || authenticationOrNull instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException(
                    "사용자를 식별할 수 없습니다. authentication: " + authenticationOrNull);
        }

        // 이제 null이 확실하게 아니므로 변수명에서 OrNull 제거.
        Authentication authentication = authenticationOrNull;

        // 인증 된 사용자의 "role" 속성 값을 가져와 Role 타입으로 반환합니다.
        // 인증 된 사용자는 항상 "role" 속성 값을 가지고 있습니다.
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            return oAuth2User.getAttribute("role");
        }
        else {
            throw new RuntimeException("지원하지 않는 authentication 객체입니다. authentication: " + authentication);
        }
    }

}
