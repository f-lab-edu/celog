package dev.sijunyang.celog.core.domain.bookmark;

import dev.sijunyang.celog.core.domain.post.PostEntity;
import dev.sijunyang.celog.core.domain.user.UserEntity;
import dev.sijunyang.celog.core.global.jpa.BaseCreateTimeEntity;
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
 * {@link UserEntity}와 {@link PostEntity} 간의 북마크 관계를 나타내는 엔티티 클래스입니다.
 *
 * @author Sijun Yang
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "bookmark")
public class BookMarkEntity extends BaseCreateTimeEntity {

    /**
     * 북마크 엔티티의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 북마크를 설정한 사용자의 고유 식별자입니다.
     */
    @NotNull
    private Long userId;

    /**
     * 북마크된 포스트의 고유 식별자입니다.
     */
    @NotNull
    private Long postId;

    @Builder
    public BookMarkEntity(Long id, Long userId, Long postId) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
    }

}
