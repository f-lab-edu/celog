package dev.sijunyang.celog.core.domain.reply;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link ReplyEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

}
