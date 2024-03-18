package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.enums.AuthenticationType;
import dev.sijunyang.celog.core.global.enums.Role;

public record CreateUserRequest(String name, String email, OauthUser oauthUser, String profileUrl,
        AuthenticationType authenticationType, Role role) {
}
