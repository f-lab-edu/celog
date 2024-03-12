package dev.sijunyang.celog.core.domain.follow;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link FollowEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

}
