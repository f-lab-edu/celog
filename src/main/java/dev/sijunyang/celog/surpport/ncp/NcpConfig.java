package dev.sijunyang.celog.surpport.ncp;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ NcpProperties.class })
@RequiredArgsConstructor
public class NcpConfig {

    private final NcpProperties ncpProperties;

    @Bean
    public S3Client customS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(this.ncpProperties.accessKey(),
                this.ncpProperties.secretKey());

        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(this.ncpProperties.region()))
            .endpointOverride(URI.create(this.ncpProperties.endPoint()))
            .build();
    }

}
