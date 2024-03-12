package dev.sijunyang.celog.core.global.enums;

import dev.sijunyang.celog.core.domain.post.PostEntity;

/**
 * {@link PostEntity}의 공개 상태를 나타내는 Enum 클래스입니다.
 *
 * @author Sijun Yang
 */
public enum PublicationStatus {

    /**
     * 모든 사용자에게 공개된 게시물 상태입니다.
     */
    PUBLIC_PUBLISHED,

    /**
     * 작성 중인 게시물 상태입니다.
     */
    DRAFTING;

}
