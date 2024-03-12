package dev.sijunyang.celog.core.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link UserEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
