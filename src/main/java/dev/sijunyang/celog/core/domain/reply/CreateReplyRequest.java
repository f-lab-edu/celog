package dev.sijunyang.celog.core.domain.reply;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;

public record CreateReplyRequest(@NotEmpty String content, @Nullable Long superReplyId) {

}
