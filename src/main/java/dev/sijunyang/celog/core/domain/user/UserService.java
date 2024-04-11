package dev.sijunyang.celog.core.domain.user;

import java.util.Map;

import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.error.nextVer.InsufficientPermissionException;
import dev.sijunyang.celog.core.global.error.nextVer.InvalidInputException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
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
        validateEmailUnique(request.email());

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
     * @param requester 수정을 요청하는 사용자 ID
     * @param userId 수정할 사용자 ID
     * @param request 수정된 사용자 정보
     */
    public void updateUser(@NotNull @Valid RequestUser requester, long userId,
            @NotNull @Valid UpdateUserRequest request) {
        validateUserEditable(requester, userId);
        validateEmailUnique(request.email());
        UserEntity oldUserEntity = this.userRepository.findById(userId)
            .orElseThrow(() -> new InvalidInputException(createUserNotFoundErrorMessage(Map.of("userId", userId))));

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
     * @param requester 삭제를 요청하는 사용자 ID
     * @param userId 삭제할 사용자 ID
     */
    public void deleteUser(@NotNull @Valid RequestUser requester, long userId) {
        validateUserEditable(requester, userId);
        this.userRepository.deleteById(userId);
    }

    /**
     * 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return 사용자 DTO
     */
    public UserDto getUserById(long userId) {
        return this.userRepository.findById(userId)
            .orElseThrow(() -> new InvalidInputException(createUserNotFoundErrorMessage(Map.of("userId", userId))))
            .toUserDto();
    }

    /**
     * 사용자 이메일로 사용자 정보를 조회합니다.
     * @param email 조회할 사용자 이메일
     * @return 사용자 DTO
     */
    public UserDto getUserByEmail(@NotNull String email) {
        return this.userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidInputException(createUserNotFoundErrorMessage(Map.of("email", email))))
            .toUserDto();
    }

    /**
     * OAuth 정보로 사용자의 존재 여부를 확인합니다.
     * @param email 조회할 사용자 이메일
     * @return 사용자가 있으면 ture, 아니면 false
     */
    public boolean existUserByEmail(@NotNull String email) {
        return this.userRepository.existsByEmail(email);
    }

    /**
     * OAuth 정보로 사용자 정보를 조회합니다.
     * @param providerName oauth 제공자 이름
     * @param oauthUserId oauth 사용자 ID
     * @return 사용자 DTO
     */
    public UserDto getUserByOAuthInfo(@NotNull String providerName, @NotNull String oauthUserId) {
        return this.userRepository.findByOauthUser_OauthProviderNameAndOauthUser_OauthUserId(providerName, oauthUserId)
            .orElseThrow(() -> new InvalidInputException(
                    createUserNotFoundErrorMessage(Map.of("providerName", providerName, "oauthUserId", oauthUserId))))
            .toUserDto();
    }

    /**
     * 사용자 이메일로 사용자의 존재 여부를 확인합니다.
     * @param providerName oauth 제공자 이름
     * @param oauthUserId oauth 사용자 ID
     * @return 사용자가 있으면 ture, 아니면 false
     */
    public boolean existUserByOAuthInfo(@NotNull String providerName, @NotNull String oauthUserId) {
        return this.userRepository.existsByOauthUser_OauthProviderNameAndOauthUser_OauthUserId(providerName,
                oauthUserId);
    }

    /**
     * 사용자가 존재하는지 검사합니다.
     * @param userId 조회할 사용자 ID
     * @throws InvalidInputException 사용자를 찾을 수 없는 경우
     */
    public void validateUserExistence(long userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new InvalidInputException("사용자의 ID가 유효하지 않습니다. userId: " + userId);
        }
    }

    private void validateEmailUnique(@NotNull String email) {
        if (this.userRepository.existsByEmail(email)) {
            throw new InvalidInputException("이미 존재하는 email 입니다. email: " + email);
        }
    }

    private void validateUserEditable(RequestUser requester, long userId) {
        validateUserExistence(requester.userId());
        validateUserExistence(userId);

        if (!(requester.userRole() == Role.ADMIN || requester.userId() == userId)) {
            throw new InsufficientPermissionException("요청하는 사용자의 권한이 부족합니다. requester: " + requester);
        }
    }

    private String createUserNotFoundErrorMessage(Map inputs) {
        return "사용자를 찾을 수 없습니다. inputs: " + inputs;
    }

}
