package dev.sijunyang.celog.core.domain.user;

import dev.sijunyang.celog.core.global.enums.AuthenticationType;
import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.jpa.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.lang.Nullable;

/**
 * 사용자 정보를 저장하는 엔티티 클래스입니다.
 *
 * @author Sijun Yang
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "users") // user는 h2 예약어이므로 사용할 수 없음
public class UserEntity extends BaseTimeEntity {

    /**
     * 사용자의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 이름입니다.
     */
    @NotNull
    private String name;

    /**
     * 사용자 이메일입니다.
     */
    @Nullable
    @Column(unique = true)
    private String email;

    /**
     * 사용자의 OAuth 관련 정보를 저장합니다. OAuth로 가입하지 않은 사용자는 null 입니다.
     */
    @Nullable
    @Embedded
    private OauthUser oauthUser;

    /**
     * 사용자 프로필 URL입니다.
     */
    @Nullable
    @Column(length = 1024)
    private String profileUrl;

    /**
     * 사용자 인증 유형을 나타냅니다.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    /**
     * 사용자 인증 권한을 나타냅니다.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public UserEntity(Long id, String name, @Nullable String email, @Nullable OauthUser oauthUser,
            @Nullable String profileUrl, AuthenticationType authenticationType, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.oauthUser = oauthUser;
        this.profileUrl = profileUrl;
        this.authenticationType = authenticationType;
        this.role = role;
    }

    /**
     * 사용자 인증 정보의 유효성을 검증합니다. <br/>
     * email과 oauthUser 중 하나는 반드시 null이 아니어야 합니다.
     * @return 유효성 검증 결과
     */
    @AssertTrue(message = "email, oauthUser 두 필드 중 하나는 null이 아니여야만 합니다.")
    private boolean valid() {
        return !(this.email == null && this.oauthUser == null);
    }

}
