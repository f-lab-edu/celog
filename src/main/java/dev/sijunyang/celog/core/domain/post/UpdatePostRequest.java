package dev.sijunyang.celog.core.domain.post;

import dev.sijunyang.celog.core.global.enums.PublicationStatus;
import jakarta.validation.constraints.NotEmpty;

public record UpdatePostRequest(@NotEmpty String title, @NotEmpty String content, PublicationStatus readStatus) {

}
