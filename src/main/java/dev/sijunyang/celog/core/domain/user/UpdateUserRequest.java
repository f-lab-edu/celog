package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(@NotNull String name, String email, String profileUrl, @NotNull Role role) {

}
