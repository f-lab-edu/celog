package dev.sijunyang.celog.surpport.ncp;

import dev.sijunyang.celog.core.global.error.nextVer.InvalidInputException;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// 통합 테스트 구현, 실제 요청이 발생하고 있고, 지금은 통합테스트 환경이 구축되지 않아 CI 시 실패하므로 주석처리하였음
// @SpringBootTest
// class ImageUploadServiceImplIntegrationTest {
//
// @Autowired
// private ImageUploadServiceImpl service;
//
// @Autowired
// private S3Template template;
//
// @Autowired
// private NcpProperties ncpProperties;
//
// @Test
// void shouldUploadImage() {
// // Given
// String fileLocation =
// "./src/test/java/dev/sijunyang/celog/surpport/ncp/test-image.jpg";
// Resource resource = new FileSystemResource(fileLocation);
//
// // When
// String result = this.service.execute(resource);
//
// // Then
// assertNotNull(result);
//
// // 테스트가 성공했으므로 필요없는 데이터는 삭제
// String filename = StringUtils.getFilename(StringUtils.uriDecode(result,
// StandardCharsets.UTF_8));
// template.deleteObject(ncpProperties.bucketName(), filename);
// }
//
// @Test
// void shouldThrowInvalidInputExceptionWhenFileExtensionIsNotSupported() {
// // Given
// String fileLocation = "./src/test/java/dev/sijunyang/celog/surpport/ncp/test-text.txt";
// Resource resource = new FileSystemResource(fileLocation);
//
// // When & Then
// assertThrows(InvalidInputException.class, () -> service.execute(resource));
// }
//
// }
