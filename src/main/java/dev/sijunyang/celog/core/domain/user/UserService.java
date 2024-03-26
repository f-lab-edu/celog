package dev.sijunyang.celog.core.domain.user;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

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
    @Transactional
    public void createUser(@NotNull @Valid CreateUserRequest request) {
        validateUniqueEmail(request.email());

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
     * @param userId 수정할 사용자 ID
     * @param request 수정된 사용자 정보
     */
    @Transactional
    public void updateUser(long userId, @NotNull @Valid UpdateUserRequest request) {
        validateUniqueEmail(request.email());

        UserEntity oldUserEntity = findUserById(userId);

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
     * @param userId 삭제할 사용자 ID
     */
    @Transactional
    public void deleteUser(long userId) {
        validUserById(userId);
        this.userRepository.deleteById(userId);
    }

    /**
     * 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return 사용자 DTO
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(long userId) {
        return findUserById(userId).toUserDto();
    }

    /**
     * 사용자 이메일로 사용자 정보를 조회합니다.
     * @param email 조회할 사용자 이메일
     * @return 사용자 DTO
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(@NotNull String email) {
        return findUserByEmail(email).toUserDto();
    }

    /**
     * OAuth 정보로 사용자 정보를 조회합니다.
     * @param providerName oauth 제공자 이름
     * @param oauthUserId oauth 사용자 ID
     * @return 사용자 DTO
     */
    @Transactional(readOnly = true)
    public UserDto getUserByOAuthInfo(@NotNull String providerName, @NotNull String oauthUserId) {
        return findUserByOAuthInfo(providerName, oauthUserId).toUserDto();
    }

    /**
     * 사용자가 존재하는지 검사합니다.
     * @param userId 조회할 사용자 ID
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public void validUserById(long userId) throws UserNotFoundException {
        if (!existUserById(userId)) {
            throw new UserNotFoundException(Map.of("userId", userId));
        }
    }

    private UserEntity findUserById(long userId) {
        return this.userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    }

    private boolean existUserById(long userId) {
        return this.userRepository.existsById(userId);
    }

    public void validateUniqueEmail(String email) throws UserNotFoundException {
        if (email == null) {
            return;
        }
        if (this.userRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException(email);
        }
    }

    private UserEntity findUserByEmail(@NotNull String email) {
        return this.userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(Map.of("email", email)));
    }

    private UserEntity findUserByOAuthInfo(@NotNull String providerName, @NotNull String oauthUserId) {
        return this.userRepository.findByOauthUser_OauthProviderNameAndOauthUser_OauthUserId(providerName, oauthUserId)
            .orElseThrow(
                    () -> new UserNotFoundException(Map.of("providerName", providerName, "oauthUserId", oauthUserId)));
    }

}
