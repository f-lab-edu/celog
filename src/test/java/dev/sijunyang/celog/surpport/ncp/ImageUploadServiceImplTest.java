package dev.sijunyang.celog.surpport.ncp;

import dev.sijunyang.celog.core.global.error.nextVer.InvalidInputException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageUploadServiceImplTest {

    @Mock
    private NcpProperties ncpProperties;

    @Mock
    private S3Template s3Template;

    @Mock
    private Resource resource;

    @Mock
    private S3Resource s3Resource;

    @InjectMocks
    private ImageUploadServiceImpl service;

    @Test
    void shouldUploadImage() throws IOException {
        // Given
        String fileExtension = "jpg";
        String filename = "filename";
        String bucketName = "bucketname";
        when(this.resource.getFilename()).thenReturn(filename + "." + fileExtension);
        when(this.resource.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        when(this.ncpProperties.bucketName()).thenReturn(bucketName);
        when(this.s3Template.upload(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class)))
            .thenReturn(this.s3Resource);
        when(this.s3Resource.getURL()).thenReturn(new java.net.URL("https://example.com/" + filename));

        // When
        String result = this.service.execute(this.resource);

        // Then
        assertNotNull(result);
    }

    @Test
    void shouldThrowInvalidInputExceptionWhenFileExtensionIsNotSupported() {
        // Given
        String fileExtension = "txt"; // not support
        String filename = "filename";
        when(this.resource.getFilename()).thenReturn(filename + "." + fileExtension);

        // When & Then
        assertFalse(ImageUploadServiceImpl.supportedExtensions.contains(fileExtension));
        assertThrows(InvalidInputException.class, () -> this.service.execute(this.resource));
    }

}
