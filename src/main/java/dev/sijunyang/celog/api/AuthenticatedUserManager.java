package dev.sijunyang.celog.api;

import dev.sijunyang.celog.core.global.enums.Role;

public interface AuthenticatedUserManager {

    Long getId();

    Role getRole();

}
