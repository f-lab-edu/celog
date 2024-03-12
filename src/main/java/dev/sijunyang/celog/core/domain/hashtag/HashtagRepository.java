package dev.sijunyang.celog.core.domain.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link HashtagEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface HashtagRepository extends JpaRepository<HashtagEntity, Long> {

}
