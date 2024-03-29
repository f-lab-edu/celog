package dev.sijunyang.celog.core.domain.user;

import java.util.Map;

import dev.sijunyang.celog.core.global.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 새로운 사용자를 생성합니다.
     * @param request 사용자 정보
     */
    public void createUser(@NotNull @Valid CreateUserRequest request) {
        validateEmail(request.email());

        UserEntity userEntity = UserEntity.builder()
            .name(request.name())
            .email(request.email())
            .oauthUser(request.oauthUser())
            .profileUrl(request.profileUrl())
            .authenticationType(request.authenticationType())
            .role(request.role())
            .build();

        this.userRepository.save(userEntity);
    }

    /**
     * 기존 사용자 정보를 수정합니다.
     * @param requestUserId 수정을 요청하는 사용자 ID
     * @param userId 수정할 사용자 ID
     * @param request 수정된 사용자 정보
     */
    public void updateUser(long requestUserId, long userId, @NotNull @Valid UpdateUserRequest request) {
        validateUserRole(requestUserId, userId);
        validateEmail(request.email());
        UserEntity oldUserEntity = this.userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

        UserEntity newEntity = UserEntity.builder()
            .id(oldUserEntity.getId())
            .name(request.name())
            .email(request.email())
            .oauthUser(oldUserEntity.getOauthUser())
            .profileUrl(request.profileUrl())
            .authenticationType(oldUserEntity.getAuthenticationType())
            .role(request.role())
            .build();

        this.userRepository.save(newEntity);
    }

    /**
     * 사용자를 삭제합니다.
     * @param requestUserId 삭제를 요청하는 사용자 ID
     * @param userId 삭제할 사용자 ID
     */
    public void deleteUser(long requestUserId, long userId) {
        validateUserRole(requestUserId, userId);
        this.userRepository.deleteById(userId);
    }

    /**
     * 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return 사용자 DTO
     */
    public UserDto getUserById(long userId) {
        return this.userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)))
            .toUserDto();
    }

    /**
     * 사용자 이메일로 사용자 정보를 조회합니다.
     * @param email 조회할 사용자 이메일
     * @return 사용자 DTO
     */
    public UserDto getUserByEmail(@NotNull String email) {
        return this.userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(Map.of("email", email)))
            .toUserDto();
    }

    /**
     * OAuth 정보로 사용자 정보를 조회합니다.
     * @param providerName oauth 제공자 이름
     * @param oauthUserId oauth 사용자 ID
     * @return 사용자 DTO
     */
    public UserDto getUserByOAuthInfo(@NotNull String providerName, @NotNull String oauthUserId) {
        return this.userRepository.findByOauthUser_OauthProviderNameAndOauthUser_OauthUserId(providerName, oauthUserId)
            .orElseThrow(
                    () -> new UserNotFoundException(Map.of("providerName", providerName, "oauthUserId", oauthUserId)))
            .toUserDto();
    }

    /**
     * 사용자가 존재하는지 검사합니다.
     * @param userId 조회할 사용자 ID
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public void validUserById(long userId) {
        this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    }

    private void validateEmail(@NotNull String email) {
        if (this.userRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException(email);
        }
    }

    private void validateUserRole(long requestUserId, long userId) {
        UserEntity requestUser = this.userRepository.findById(requestUserId)
            .orElseThrow(() -> new UserNotFoundException(Map.of("userId", requestUserId)));
        if (!(requestUser.getRole() == Role.ADMIN || requestUser.getId() == userId)) {
            throw new AccessDeniedException("권한이 부족한 사용자입니다. ID: " + requestUserId);
        }
    }

}
