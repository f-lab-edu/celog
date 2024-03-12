package dev.sijunyang.celog.core.global.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * {@link EnableJpaAuditing} 어노테이션을 사용하여 Spring Data JPA의 Auditing 기능을 활성화합니다.
 *
 * @author Sijun Yang
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

}
