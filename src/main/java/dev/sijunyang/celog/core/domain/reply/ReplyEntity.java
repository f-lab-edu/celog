package dev.sijunyang.celog.core.domain.reply;

import dev.sijunyang.celog.core.global.jpa.BaseTimeEntity;
import dev.sijunyang.celog.core.domain.post.PostEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.lang.Nullable;

/**
 * 어떤 {@link PostEntity}에 대한 댓글 정보를 저장하는 엔티티 클래스입니다.
 *
 * @author Sijun Yang
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "reply")
public class ReplyEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 댓글 내용입니다.
     */
    @NotNull
    @Column(length = 4096)
    private String content;

    /**
     * 댓글을 작성한 사용자의 고유 식별자입나다.
     */
    @NotNull
    private Long userId;

    /**
     * 댓글이 작성된 포스트의 고유 식별자입니다.
     */
    @NotNull
    private Long postId;

    /**
     * 대댓글인 경우, 상위 댓글의 고유 식별자입니다. 상위 댓글이 없는 경우 null 입니다.
     */
    @Nullable
    private Long superReplyId;

    @Builder
    public ReplyEntity(Long id, String content, Long userId, Long postId, @Nullable Long superReplyId) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.superReplyId = superReplyId;
    }

}
