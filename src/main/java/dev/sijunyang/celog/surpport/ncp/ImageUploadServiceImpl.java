package dev.sijunyang.celog.surpport.ncp;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import dev.sijunyang.celog.core.domain.file.ImageUploadService;
import dev.sijunyang.celog.core.global.error.nextVer.InvalidInputException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ImageUploadServiceImpl implements ImageUploadService {

    /**
     * 지원하는 확장자 목록입니다.
     */
    public static final List<String> supportedExtensions = List.of("png", "svg", "jpg", "jpeg", "gif");

    private final NcpProperties ncpProperties;

    private final S3Template s3Template;

    @Override
    public String execute(Resource resource) {
        String fileExtension = StringUtils.getFilenameExtension(resource.getFilename());
        if (!isSupportedFileExtension(fileExtension)) {
            throw new InvalidInputException(
                    "지원하지 않는 file 확장자입니다. input: " + fileExtension + "supportExtensions: " + supportedExtensions);
        }

        S3Resource s3Resource = this.s3Template.upload(this.ncpProperties.bucketName(), createFileName(fileExtension),
                getInputStream(resource), ObjectMetadata.builder().acl(ObjectCannedACL.PUBLIC_READ).build());
        return getUrlString(s3Resource);
    }

    private String getUrlString(Resource resource) {
        try {
            return resource.getURL().toString();
        }
        catch (IOException ex) {
            throw new RuntimeException("저장된 이미지 S3Resource의 URL을 가져올 수 없습니다", ex);
        }
    }

    private InputStream getInputStream(Resource resource) {
        try {
            return resource.getInputStream();
        }
        catch (IOException ex) {
            // 특정 의존성 (spring core 제외)을 가지지 않는 적절한 UnChecked 예외가 없어서 RuntimeException 으로
            // 설정
            throw new RuntimeException("이미지 파일의 InputStream을 가져올 수 없습니다", ex);
        }
    }

    private String createFileName(String fileExtension) {
        return LocalDateTime.now() + "_" + UUID.randomUUID() + "." + fileExtension;
    }

    private boolean isSupportedFileExtension(String fileExtension) {
        return supportExtensions.stream().anyMatch((supportExtension) -> Objects.equals(supportExtension, fileExtension));
    }

}
