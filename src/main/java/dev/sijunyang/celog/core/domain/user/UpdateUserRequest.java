package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.enums.Role;

public record UpdateUserRequest(String name, String email, String profileUrl, Role role) {

}
