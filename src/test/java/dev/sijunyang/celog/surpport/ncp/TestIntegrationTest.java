package dev.sijunyang.celog.surpport.ncp;

import dev.sijunyang.celog.CelogApplication;
import dev.sijunyang.celog.core.domain.user.UserEntity;
import dev.sijunyang.celog.core.domain.user.UserRepository;
import dev.sijunyang.celog.core.global.enums.AuthenticationType;
import dev.sijunyang.celog.core.global.enums.Role;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = {CelogApplication.class})
class TestIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NcpProperties ncpProperties;

    @Test
    void shouldGetNcpProperties() {
        userRepository.save(UserEntity.builder()
            .id(null)
            .name("name")
            .profileUrl("profileUrl")
            .email("email")
            .authenticationType(AuthenticationType.EMAIL)
            .role(Role.USER)
            .build());
        List<UserEntity> rt = userRepository.findAll();

        assertFalse(rt.isEmpty());
        assertNotNull(ncpProperties);
        assertNotNull(ncpProperties.region());
        assertNotNull(ncpProperties.accessKey());
        assertNotNull(ncpProperties.endPoint());
    }

}
