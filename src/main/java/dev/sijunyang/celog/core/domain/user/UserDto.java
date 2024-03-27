package dev.sijunyang.celog.core.domain.user;

import java.time.LocalDateTime;

import dev.sijunyang.celog.core.global.enums.AuthenticationType;
import dev.sijunyang.celog.core.global.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {

    private final Long id;

    private final String name;

    private final String email;

    private final OauthUser oauthUser;

    private final String profileUrl;

    private final AuthenticationType authenticationType;

    private final Role role;

    private final LocalDateTime modifiedAt;

    private final LocalDateTime createdAt;

    @Builder
    public UserDto(Long id, String name, String email, OauthUser oauthUser, String profileUrl,
            AuthenticationType authenticationType, Role role, LocalDateTime modifiedAt, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileUrl = profileUrl;
        this.oauthUser = oauthUser;
        this.authenticationType = authenticationType;
        this.role = role;
        this.modifiedAt = modifiedAt;
        this.createdAt = createdAt;
    }

}
