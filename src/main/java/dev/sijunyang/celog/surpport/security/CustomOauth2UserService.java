package dev.sijunyang.celog.surpport.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dev.sijunyang.celog.core.domain.user.CreateUserRequest;
import dev.sijunyang.celog.core.domain.user.OAuth2Provider;
import dev.sijunyang.celog.core.domain.user.OauthUser;
import dev.sijunyang.celog.core.domain.user.UserDto;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.enums.AuthenticationType;
import dev.sijunyang.celog.core.global.enums.Role;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    private final DefaultOAuth2UserService delegateOauth2UserService = new DefaultOAuth2UserService();

    public CustomOauth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = this.delegateOauth2UserService.loadUser(userRequest);
        String providerName = userRequest.getClientRegistration().getRegistrationId();
        UserDto createdUserDto = findOrCreateUserByProvider(providerName, oAuth2User);
        return createAuthenticationToken(createdUserDto, oAuth2User);
    }

    private UserDto findOrCreateUserByProvider(String providerName, OAuth2User oAuth2User) {
        OAuthUserInfo oAuthUserInfo = getOAuthUserInfo(providerName, oAuth2User);
        if (oAuthUserInfo == null) {
            throw new InternalAuthenticationServiceException("지원하지 않는 OAuth2 제공자: " + providerName);
        }

        String email = oAuthUserInfo.getEmail();
        String oauthUserId = oAuth2User.getName();
        UserDto userDto = getUserByEmailOrOAuthInfo(email, providerName, oauthUserId);

        if (userDto == null) {
            userDto = createUserByProvider(providerName, oAuthUserInfo);
            if (userDto == null) {
                throw new InternalAuthenticationServiceException(
                        "OAuth2 인증 중, 새로 생성된 사용자를 가져오는데 문제가 발생했습니다. OAuth2User: " + oAuth2User);
            }
        }

        return userDto;
    }

    private OAuthUserInfo getOAuthUserInfo(String providerName, OAuth2User oAuth2User) {
        return switch (providerName) {
            case OAuth2Provider.GITHUB -> new GithubOAuthUserInfo(oAuth2User);
            default -> null;
        };
    }

    private UserDto createUserByProvider(String providerName, OAuthUserInfo oAuthUserInfo) {
        String oauthUserId = oAuthUserInfo.getName();
        String name = oAuthUserInfo.getName();
        String email = oAuthUserInfo.getEmail();
        String avatarUrl = oAuthUserInfo.getProfileUrl();

        OauthUser oauthUser = new OauthUser(providerName, oauthUserId);
        CreateUserRequest createUserRequest = new CreateUserRequest(name, email, oauthUser, avatarUrl,
                AuthenticationType.OAUTH_GITHUB, Role.USER);

        this.userService.createUser(createUserRequest);

        return getUserByEmailOrOAuthInfo(email, providerName, oauthUserId);
    }

    /**
     * 이메일 또는 OAuth 정보로 사용자를 찾습니다. 기본적으로 이메일로 사용자를 식별하나, 이메일이 존재하지 않으면 OAuth 정보로 사용자를
     * 찾습니다.
     * @param email 사용자의 이메일 주소 (nullable)
     * @param oAuthProviderName oAuth 제공사의 이름
     * @param oauthUserId 사용자의 OAuth 고유 식별자
     * @return 사용자 DTO 객체, 사용자를 찾지 못한 경우 null
     */
    private UserDto getUserByEmailOrOAuthInfo(String email, String oAuthProviderName, String oauthUserId) {
        if (email != null && this.userService.existUserByEmail(email)) {
            return this.userService.getUserByEmail(email);
        }
        else if (this.userService.existUserByOAuthInfo(oAuthProviderName, oauthUserId)) {
            return this.userService.getUserByOAuthInfo(oAuthProviderName, oauthUserId);
        }
        return null;
    }

    private OAuth2User createAuthenticationToken(UserDto userDto, OAuth2User oAuth2User) {
        Role role = userDto.getRole();
        String nameAttributeKey = "celog_user_id";
        Map<String, Object> attributes = new HashMap<>(Map.of("celog_user_id", userDto.getId(), "celog_role", role));
        attributes.putAll(oAuth2User.getAttributes());
        Collection<GrantedAuthority> authorities = new ArrayList<>(oAuth2User.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }

}
