package dev.sijunyang.celog.core.domain.post;

import java.time.LocalDateTime;

public record PostSummaryDto(Long postId, String title, Long userId, LocalDateTime modifiedAt,
        LocalDateTime createdAt) {

}
