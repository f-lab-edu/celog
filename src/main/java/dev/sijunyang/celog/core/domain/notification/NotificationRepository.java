package dev.sijunyang.celog.core.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link NotificationEntity}를 처리하는 JpaRepository입니다.
 *
 * @author Sijun Yang
 */
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

}
