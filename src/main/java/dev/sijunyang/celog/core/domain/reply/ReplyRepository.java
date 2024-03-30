package dev.sijunyang.celog.core.domain.reply;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * {@link ReplyEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

    // depth 0 댓글을 가져온다.
    List<ReplyEntity> findAllByPostIdAndSuperReplyIdIsNull(Long postId);

    List<ReplyEntity> findAllBySuperReplyId(Long superReplyId);

}
