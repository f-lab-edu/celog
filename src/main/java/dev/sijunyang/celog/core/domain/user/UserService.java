package dev.sijunyang.celog.core.domain.user;

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
     * @return 생성된 사용자 DTO
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (this.userRepository.existsByEmail(request.email())) {
            throw new DuplicatedEmailException(request.email());
        }

        UserEntity userEntity = UserEntity.builder()
            .name(request.name())
            .email(request.email())
            .oauthUser(request.oauthUser())
            .profileUrl(request.profileUrl())
            .authenticationType(request.authenticationType())
            .role(request.role())
            .build();

        UserEntity savedEntity = this.userRepository.save(userEntity);

        return UserDto.convert(savedEntity);
    }

    /**
     * 기존 사용자 정보를 전체 수정합니다.
     * @param userId 수정할 사용자 ID
     * @param request 수정된 사용자 정보
     * @return 수정된 사용자 DTO
     */
    @Transactional
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        UserEntity oldUserEntity = findUserById(userId);

        UserEntity updatedEntity = UserEntity.builder()
            .id(oldUserEntity.getId())
            .name(request.name())
            .email(request.email())
            .oauthUser(oldUserEntity.getOauthUser())
            .profileUrl(request.profileUrl())
            .authenticationType(oldUserEntity.getAuthenticationType())
            .role(request.role())
            .build();

        return UserDto.convert(updatedEntity);
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
        return UserDto.convert(findUserById(userId));
    }

    public UserEntity findUserById(Long userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

}
