package dev.sijunyang.celog.core.domain.notification;

import dev.sijunyang.celog.core.global.jpa.BaseCreateTimeEntity;
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

/**
 * 알림 정보를 저장하는 엔티티 클래스입니다.
 *
 * @author Sijun Yang
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "notification")
public class NotificationEntity extends BaseCreateTimeEntity {

    /**
     * 알림의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 알림 내용입니다.
     */
    @NotNull
    @Column(length = 1024)
    private String content;

    /**
     * 알림을 읽었는지 여부를 나타냅니다. true면 읽음, false면 읽지 않음 상태를 의미합니다.
     */
    @NotNull
    private Boolean readStatus;

    @Builder
    public NotificationEntity(Long id, String content, Boolean readStatus) {
        this.id = id;
        this.content = content;
        this.readStatus = readStatus;
    }

}
