package dev.sijunyang.celog.surpport.security;

import dev.sijunyang.celog.api.AuthenticatedUserManager;
import dev.sijunyang.celog.core.global.enums.Role;

import org.springframework.security.core.context.SecurityContextHolder;
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
        // OAuth2 사용자의 이름(명)을 가져와 Long 타입으로 변환하여 반환합니다.
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(oAuth2User.getName());
    }

    @Override
    public Role getRole() {
        // OAuth2 사용자의 "role" 속성 값을 가져와 Role 타입으로 반환합니다.
        // 인증 된 사용자는 항상 "role" 속성 값을 가지고 있습니다.
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return oAuth2User.getAttribute("role");
    }

}
