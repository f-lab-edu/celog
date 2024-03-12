package dev.sijunyang.celog.core.domain.follow;

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
 * 두 {@link UserEntity} 사이의 팔로우 관계를 나타내는 엔티티 클래스입니다.
 *
 * @author Sijun Yang
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "tbl_follow")
public class FollowEntity extends BaseCreateTimeEntity {

    /**
     * 팔로우 엔티티의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 팔로우 되는 사용자의 고유 식별자입니다.
     */
    @NotNull
    private Long followerId;

    /**
     * 팔로우하는 사용자의 고유 식별자입니다.
     */
    @NotNull
    private Long followingId;

    @Builder
    public FollowEntity(Long id, Long followerId, Long followingId) {
        this.id = id;
        this.followerId = followerId;
        this.followingId = followingId;
    }

}
