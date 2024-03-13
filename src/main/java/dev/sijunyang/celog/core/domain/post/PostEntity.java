package dev.sijunyang.celog.core.domain.post;

import dev.sijunyang.celog.core.global.enums.PublicationStatus;
import dev.sijunyang.celog.core.global.jpa.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포스트(게시글) 정보를 저장하는 엔티티 클래스입니다.
 *
 * @see PostHashtagEntity
 * @author Sijun Yang
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "tbl_post")
public class PostEntity extends BaseTimeEntity {

    /**
     * 포스트의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 포스트 제목입니다.
     */
    @NotNull
    private String title;

    /**
     * 포스트 내용입니다.
     */
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 포스트 공개 상태를 나타냅니다.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private PublicationStatus readStatus;

    /**
     * 포스트를 작성한 사용자의 고유 식별자입니다.
     */
    @NotNull
    private Long userId;

    @Builder
    public PostEntity(Long id, String title, String content, PublicationStatus readStatus, Long userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.readStatus = readStatus;
        this.userId = userId;
    }

}
