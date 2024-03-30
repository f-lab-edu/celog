package dev.sijunyang.celog.core.domain.reply;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link ReplyEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

    List<ReplyEntity> deleteAllByPostId(Long postId);

    // depth 0 댓글을 가져온다.
    List<ReplyEntity> findAllByPostIdAndSuperReplyIdIsNull(Long postId);

    List<ReplyEntity> findAllBySuperReplyId(Long superReplyId);

}
