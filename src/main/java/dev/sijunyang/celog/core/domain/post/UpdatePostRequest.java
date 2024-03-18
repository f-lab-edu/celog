package dev.sijunyang.celog.core.domain.post;

import dev.sijunyang.celog.core.global.enums.PublicationStatus;

public record UpdatePostRequest(String title, String content, PublicationStatus readStatus) {

}

