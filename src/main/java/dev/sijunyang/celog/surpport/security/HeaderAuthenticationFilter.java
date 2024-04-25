package dev.sijunyang.celog.surpport.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.error.nextVer.InvalidInputException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    // 테스트 시 다음과 같은 curl 사용 가능
    // curl -X GET \
    // -H "TEST_AUTH: true" \
    // -b "USER_ID=123;USER_ROLE=ADMIN" \
    // http://localhost:8080/some-endpoint

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String testAuth = request.getHeader("TEST_AUTH");
        if (testAuth == null || !testAuth.equals("true")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new InvalidInputException(
                    "TEST_AUTH 기능을 사용하기 위해선 USER_ID와 USER_ROLE 쿠키가 설정되어야 합니다. 아무 쿠키도 입력되지 않았습니다.");
        }

        String userId = null;
        String userRole = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("USER_ID")) {
                userId = cookie.getValue();
            }
            else if (cookie.getName().equals("USER_ROLE")) {
                userRole = cookie.getValue();
            }
        }

        if (userId == null || userRole == null) {
            throw new InvalidInputException(
                    "TEST_AUTH 기능을 사용하기 위해선 USER_ID와 USER_ROLE 쿠키가 설정되어야 합니다. cookies: " + Arrays.toString(cookies));
        }

        Role role = Role.valueOf(userRole);
        Map<String, Object> attributes = Map.of(CustomOauth2UserService.CELOG_USER_ID, Long.parseLong(userId),
                CustomOauth2UserService.CELOG_ROLE, role);

        // TODO 왜 Collections.singleton가 꼭 필요한거지?
        Collection<GrantedAuthority> authorities = Collections
            .singleton(new SimpleGrantedAuthority(CustomOauth2UserService.ROLE_PREFIX + role.name()));

        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(
                new DefaultOAuth2User(authorities, attributes, CustomOauth2UserService.CELOG_USER_ID),
                Collections.emptyList(), "test-by-celog");

        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }

}
