package dev.sijunyang.celog.core.domain.reply;

import jakarta.validation.constraints.NotEmpty;

public record UpdateReplyRequest(@NotEmpty String content) {

}
