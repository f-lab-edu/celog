package dev.sijunyang.celog.core.global.jpa;

import java.time.LocalDateTime;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 생성 시간을 기록하는 엔티티를 위한 기본 클래스입니다.
 *
 * @author Sijun Yang
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseCreateTimeEntity {

    /**
     * 엔티티가 생성된 시간입니다.
     */
    @CreatedDate
    private LocalDateTime createdAt;

}
