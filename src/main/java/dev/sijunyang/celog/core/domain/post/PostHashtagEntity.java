package dev.sijunyang.celog.core.domain.post;

import dev.sijunyang.celog.core.domain.hashtag.HashtagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글과 해시태그 간의 관계를 나타내는 엔티티입니다. <br/>
 * 하나의 게시글은 여러 개의 해시태그를 가지거나 아무 해시태그도 가지지 않을 수 있습니다.
 *
 * @author Sijun Yang
 * @see PostEntity
 * @see HashtagEntity
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "post_hashtag")
public class PostHashtagEntity {

    /**
     * 게시글-해시태그 관계의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 해시태그를 사용하는 포스트의 고유 식별자입니다.
     */
    @NotNull
    private Long postId;

    /**
     * 포스트가 사용하는 해시태그의 식별자입니다.
     */
    @NotNull
    private Long hashtagId;

    @Builder
    public PostHashtagEntity(Long id, Long postId, Long hashtagId) {
        this.id = id;
        this.postId = postId;
        this.hashtagId = hashtagId;
    }

}
