package dev.sijunyang.celog.core.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link PostEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface PostRepository extends JpaRepository<PostEntity, Long> {

}
