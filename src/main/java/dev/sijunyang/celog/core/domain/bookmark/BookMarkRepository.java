package dev.sijunyang.celog.core.domain.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link BookMarkEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface BookMarkRepository extends JpaRepository<BookMarkEntity, Long> {

}
