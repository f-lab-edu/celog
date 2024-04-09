package dev.sijunyang.celog.surpport.ncp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ncp")
public record NcpProperties(String endPoint, String region, String accessKey, String secretKey, String bucketName) {
}
