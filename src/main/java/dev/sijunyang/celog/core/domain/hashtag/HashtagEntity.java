package dev.sijunyang.celog.core.domain.hashtag;

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
 * 해시태그 정보를 저장하는 엔티티입니다.
 *
 * @author Sijun Yang
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "hashtag")
public class HashtagEntity extends BaseCreateTimeEntity {

    /**
     * 해시태그의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 해시태그 이름입니다.
     */
    @NotNull
    private String name;

    @Builder
    public HashtagEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
