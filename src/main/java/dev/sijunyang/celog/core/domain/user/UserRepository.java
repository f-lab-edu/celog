package dev.sijunyang.celog.core.domain.user;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link UserEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(@NotNull String email);

    Optional<UserEntity> findByOauthUser_OauthProviderNameAndOauthUser_OauthUserId(@NotNull String oauthProviderName,
            @NotNull String oauthUserId);

    boolean existsByOauthUser_OauthProviderNameAndOauthUser_OauthUserId(@NotNull String oauthProviderName,
            @NotNull String oauthUserId);

}
