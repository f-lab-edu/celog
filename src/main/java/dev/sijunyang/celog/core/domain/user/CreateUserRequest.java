package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.enums.AuthenticationType;
import dev.sijunyang.celog.core.global.enums.Role;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(@NotNull String name, String email, OauthUser oauthUser, String profileUrl,
        @NotNull AuthenticationType authenticationType, @NotNull Role role) {
}
