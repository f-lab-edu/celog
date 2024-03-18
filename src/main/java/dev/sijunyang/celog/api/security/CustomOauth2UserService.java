package dev.sijunyang.celog.api.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import dev.sijunyang.celog.core.domain.user.CreateUserRequest;
import dev.sijunyang.celog.core.domain.user.OauthUser;
import dev.sijunyang.celog.core.domain.user.UserDto;
import dev.sijunyang.celog.core.domain.user.UserNotFoundException;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.enums.AuthenticationType;
import dev.sijunyang.celog.core.global.enums.Role;

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

    public CustomOauth2UserService(UserService service) {
        this.userService = service;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = this.delegateOauth2UserService.loadUser(userRequest);

        String providerName = userRequest.getClientRegistration().getRegistrationId();
        UserDto createdUserDto = findUser(providerName, oAuth2User)
            .orElseGet(() -> createUser(providerName, oAuth2User));

        Role role = createdUserDto.getRole();
        String nameAttributeKey = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
        Map<String, Object> attributes = new HashMap<>(
                Map.of("celog_user_id", createdUserDto.getId(), "celog_role", role));
        attributes.putAll(oAuth2User.getAttributes());
        Collection<GrantedAuthority> authorities = new ArrayList<>(oAuth2User.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);

    }

    private Optional<UserDto> findUser(String providerName, OAuth2User oAuth2User) {
        try {
            if (providerName.equals("github")) {
                String oauthUserId = oAuth2User.getName();
                String email = oAuth2User.getAttribute("email");
                if (email == null) {
                    return Optional.of(this.userService.getUserByOAuthInfo(providerName, oauthUserId));
                }
                return Optional.of(this.userService.getUserByEmail(email));
            }
            else {
                // 아직 다른 OAuth2 Provider Login 기능 지원하지 않음
                throw new IllegalArgumentException();
            }
        }
        catch (UserNotFoundException ex) {
            return Optional.empty();
        }
    }

    private UserDto createUser(String providerName, OAuth2User oAuth2User) {
        if (providerName.equals("github")) {
            CreateUserRequest createUserRequest = new CreateUserRequest(oAuth2User.getAttribute("name"),
                    oAuth2User.getAttribute("email"), new OauthUser(providerName, oAuth2User.getName()),
                    oAuth2User.getAttribute("avatar_url"), AuthenticationType.OAUTH_GITHUB, Role.USER);
            return this.userService.createUser(createUserRequest);
        }
        else {
            // 아직 다른 OAuth2 Provider Login 기능 지원하지 않음
            throw new IllegalArgumentException();
        }
    }

}
