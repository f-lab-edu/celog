package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.enums.AuthenticationType;
import dev.sijunyang.celog.core.global.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateNewUser() {
        // Given
        Long userId = 1L;
        OauthUser oauthUser = new OauthUser("github", "12345678");
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", oauthUser,
                "https://profile/img-1234", AuthenticationType.OAUTH_GITHUB, Role.USER);
        UserEntity createdUserEntity = UserEntity.builder()
            .id(userId)
            .name("John Doe")
            .email("john@example.com")
            .oauthUser(oauthUser)
            .profileUrl("https://profile/img-1234")
            .authenticationType(AuthenticationType.OAUTH_GITHUB)
            .role(Role.USER)
            .build();

        when(userRepository.save(any())).thenReturn(createdUserEntity);

        // When
        userService.createUser(request);

        // Then
        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(userRepository, times(1)).save(any());

        UserEntity savedUser = userRepository.save(any());
        assertNotNull(savedUser);
        assertEquals(userId, savedUser.getId());
        assertEquals(createdUserEntity.getId(), savedUser.getId());
        assertEquals(createdUserEntity.getName(), savedUser.getName());
        assertEquals(createdUserEntity.getEmail(), savedUser.getEmail());
        assertEquals(createdUserEntity.getOauthUser(), savedUser.getOauthUser());
        assertEquals(createdUserEntity.getProfileUrl(), savedUser.getProfileUrl());
        assertEquals(createdUserEntity.getAuthenticationType(), savedUser.getAuthenticationType());
        assertEquals(createdUserEntity.getRole(), savedUser.getRole());
    }

    @Test
    void shouldUpdateExistingUser() {
        // Given
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest("John Smith", "john.smith@example.com", null, Role.ADMIN);
        UserEntity expectedUserEntity = UserEntity.builder()
            .id(userId)
            .name("John Smith")
            .email("john.smith@example.com")
            .oauthUser(null)
            .profileUrl(null)
            .authenticationType(AuthenticationType.EMAIL)
            .role(Role.ADMIN)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUserEntity));

        // When
        userService.updateUser(userId, request);

        // Then
        // 의존하는 Mock 객체가 올바르게 호출되었는가?
        verify(userRepository, times(1)).findById(userId);
        assertEquals(expectedUserEntity.getId(), userId);
        assertEquals(expectedUserEntity.getName(), request.name());
        assertEquals(expectedUserEntity.getEmail(), request.email());
        assertEquals(expectedUserEntity.getProfileUrl(), request.profileUrl());
        assertEquals(expectedUserEntity.getRole(), request.role());

        UserEntity updatedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(request.name(), updatedUser.getName());
        assertEquals(request.email(), updatedUser.getEmail());
    }

    @Test
    void shouldDeleteExistingUser() {
        // Given
        Long userId = 1L;
        UserEntity existingUserEntity = UserEntity.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUserEntity));

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository, times(1)).delete(existingUserEntity);
    }

    @Test
    void shouldReturnExistingUser() {
        // Given
        Long userId = 1L;
        UserEntity existingUserEntity = UserEntity.builder()
            .id(userId)
            .name("John Doe")
            .email("john@example.com")
            .authenticationType(AuthenticationType.EMAIL)
            .role(Role.USER)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUserEntity));

        // When
        UserDto retrievedDto = userService.getUserById(userId);

        // Then
        assertNotNull(retrievedDto);
        assertEquals(existingUserEntity.getId(), retrievedDto.getId());
        assertEquals(existingUserEntity.getName(), retrievedDto.getName());
        assertEquals(existingUserEntity.getEmail(), retrievedDto.getEmail());
        assertEquals(existingUserEntity.getAuthenticationType(), retrievedDto.getAuthenticationType());
        assertEquals(existingUserEntity.getRole(), retrievedDto.getRole());
    }

    @Test
    void shouldThrowDuplicatedEmailException() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", null,
                "https://profile/img-1234", AuthenticationType.OAUTH_GITHUB, Role.USER);

        doReturn(true).when(userRepository).existsByEmail(request.email());

        // When & Then
        assertThrows(DuplicatedEmailException.class, () -> userService.createUser(request));
    }

    @Test
    void shouldThrowUserNotFoundException() {
        // Given
        Long userId = 1L;

        doReturn(Optional.empty()).when(userRepository).findById(userId);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

}
