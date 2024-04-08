package dev.sijunyang.celog.core.domain.file;

import org.springframework.core.io.Resource;

public interface ImageUploadService {

    String execute(Resource resource);

}
