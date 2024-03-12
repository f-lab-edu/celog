package dev.sijunyang.celog.core.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link PostHashtagEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface PostHashtagRepository extends JpaRepository<PostHashtagEntity, Long> {

}
