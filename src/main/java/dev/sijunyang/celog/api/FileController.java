package dev.sijunyang.celog.api;

import java.net.URI;
import java.util.Map;

import dev.sijunyang.celog.core.domain.file.ImageUploadService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/image", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> imageUpload(@RequestPart("image") MultipartFile file) {
        String storedImageUrl = this.imageUploadService.execute(file.getResource());
        return ResponseEntity.created(URI.create(storedImageUrl)).build();
    }

}
