package dev.sijunyang.celog.core.domain.reply;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public final class ReplyDto {

    private Long replyId;

    private String content;

    private Long userId;

    private Long postId;

    private Long superReplyId;

    private LocalDateTime modifiedAt;

    private LocalDateTime createdAt;

    @Builder
    public ReplyDto(Long replyId, String content, Long userId, Long postId, Long superReplyId, LocalDateTime modifiedAt,
            LocalDateTime createdAt) {
        this.replyId = replyId;
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.superReplyId = superReplyId;
        this.modifiedAt = modifiedAt;
        this.createdAt = createdAt;
    }

}
