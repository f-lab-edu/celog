package dev.sijunyang.celog.core.domain.post;

import java.time.LocalDateTime;

import dev.sijunyang.celog.core.global.enums.PublicationStatus;

public record PostDto(Long postId, String title, String content, PublicationStatus readStatus, Long userId,
        LocalDateTime modifiedAt, LocalDateTime createdAt) {

}
