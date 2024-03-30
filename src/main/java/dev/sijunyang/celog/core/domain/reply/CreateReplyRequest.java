package dev.sijunyang.celog.core.domain.reply;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateReplyRequest(@NotEmpty String content, @NotNull Long postId, @Nullable Long superReplyId) {

}
