package dev.sijunyang.celog.surpport.security;

import java.util.Collections;
import java.util.Map;

import dev.sijunyang.celog.core.domain.user.UserDto;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOauth2UserServiceTest {

    @Mock
    private OAuth2UserRequest oAuth2UserRequest;

    @Mock
    private ClientRegistration clientRegistration;

    @Mock
    private UserService userService;

    @Mock
    private DefaultOAuth2UserService delegateOauth2UserService;

    @InjectMocks
    private CustomOauth2UserService customOauth2UserService;

    @Test
    void shouldCreateNewOAuthUser() {
        // Given
        OAuth2User willReturnOAuth2User = new DefaultOAuth2User(Collections.emptySet(), Map.of("avatar_url",
                "https:img-url.com/1234", "id", 12345678, "name", "UserName1", "email", "userEmail@gmail.com"), "id");

        when(oAuth2UserRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("github");
        when(delegateOauth2UserService.loadUser(oAuth2UserRequest)).thenReturn(willReturnOAuth2User);
        when(userService.existUserByEmail("userEmail@gmail.com")).thenReturn(false);
        when(userService.existUserByEmail("userEmail@gmail.com")).thenReturn(true);
        when(userService.getUserByEmail("userEmail@gmail.com"))
            .thenReturn(UserDto.builder().id(1L).role(Role.USER).build());

        // When
        OAuth2User rt = customOauth2UserService.loadUser(oAuth2UserRequest);

        // Then
        Assertions.assertEquals(rt.getAttribute("avatar_url"), "https:img-url.com/1234");
        Assertions.assertEquals(rt.getName(), "1");
        Assertions.assertEquals((Integer) rt.getAttribute("id"), 12345678);
        Assertions.assertEquals(rt.getAttribute("name"), "UserName1");
        Assertions.assertEquals(rt.getAttribute("email"), "userEmail@gmail.com");
        Assertions.assertEquals(rt.getAttribute("celog_role"), Role.USER);

    }

    @Test
    void shouldThrowExWhenUnSupportOAuth2Provider() {
        // Given
        when(oAuth2UserRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("unSupportOAuth2Provider");

        // When & Then
        Assertions.assertThrows(InternalAuthenticationServiceException.class, () -> customOauth2UserService.loadUser(oAuth2UserRequest));

    }

    // @Test
    // void shouldGetOAuthUser() {
    // // Given
    // Mockito.when(userService.)
    //
    // // When
    // OAuth2User rt = customOauth2UserService.loadUser(dummyOAuth2UserRequest);
    //
    // // Then
    //
    //
    // }

}
