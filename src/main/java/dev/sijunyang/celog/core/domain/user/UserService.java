package dev.sijunyang.celog.core.domain.user;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 새로운 사용자를 생성합니다.
     * @param request 사용자 정보
     */
    @Transactional
    public void createUser(CreateUserRequest request) {
        validEmail(request.email());

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
    public void updateUser(Long userId, UpdateUserRequest request) {
        validEmail(request.email());

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
    public void deleteUser(Long userId) {
        UserEntity userEntity = findUserById(userId);
        this.userRepository.delete(userEntity);
    }

    /**
     * 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return 사용자 DTO
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        return mapToUserDto(findUserById(userId));
    }

    /**
     * 사용자 이메일로 사용자 정보를 조회합니다.
     * @param email 조회할 사용자 이메일
     * @return 사용자 DTO
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return mapToUserDto(findUserById(email));
    }

    /**
     * OAuth 정보로 사용자 정보를 조회합니다.
     * @param providerName oauth 제공자 이름
     * @param oauthUserId oauth 사용자 ID
     * @return 사용자 DTO
     */
    @Transactional(readOnly = true)
    public UserDto getUserByOAuthInfo(String providerName, String oauthUserId) {
        return mapToUserDto(findUserByOAuthInfo(providerName, oauthUserId));
    }

    /**
     * 사용자가 존재하는지 검사합니다.
     * @param userId 조회할 사용자 ID
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public void validUserById(Long userId) throws UserNotFoundException {
        if (!existUserById(userId)) {
            throw new UserNotFoundException(Map.of("userId", userId));
        }
    }

    private UserEntity findUserById(Long userId) {
        return this.userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    }

    private boolean existUserById(Long userId) {
        return this.userRepository.existsById(userId);
    }

    public void validEmail(String email) throws UserNotFoundException {
        if (this.userRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException(email);
        }
    }

    private UserEntity findUserById(String email) {
        return this.userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(Map.of("email", email)));
    }

    private UserEntity findUserByOAuthInfo(String providerName, String oauthUserId) {
        return this.userRepository.findByOauthUser_OauthProviderNameAndOauthUser_OauthUserId(providerName, oauthUserId)
            .orElseThrow(
                    () -> new UserNotFoundException(Map.of("providerName", providerName, "oauthUserId", oauthUserId)));
    }

    public UserDto mapToUserDto(UserEntity user) {
        return UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .profileUrl(user.getProfileUrl())
            .oauthUser(user.getOauthUser())
            .authenticationType(user.getAuthenticationType())
            .role(user.getRole())
            .modifiedAt(user.getModifiedAt())
            .createdAt(user.getCreatedAt())
            .build();
    }

}
