package dev.sijunyang.celog.core.domain.post;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.sijunyang.celog.core.domain.reply.ReplyService;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.enums.PublicationStatus;
import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.error.nextVer.InsufficientAuthorizationException;
import dev.sijunyang.celog.core.global.error.nextVer.NotFoundResourceException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserService userService;

    private final ReplyService replyService;

    /**
     * 새로운 게시글을 생성합니다.
     * @param userId 게시글을 작성할 사용자 ID
     * @param createPostRequest 생성할 게시글 정보
     */
    public void createPost(long userId, @Valid CreatePostRequest createPostRequest) {
        validUserId(userId);
        PostEntity postEntity = PostEntity.builder()
            .id(null)
            .title(createPostRequest.title())
            .content(createPostRequest.content())
            .readStatus(createPostRequest.readStatus())
            .userId(userId)
            .build();
        this.postRepository.save(postEntity);
    }

    /**
     * 기존 게시글 정보를 수정합니다. 게시글을 소유한 사용자만 수행할 수 있습니다.
     * @param requestUserId 게시글을 수정하려는 사용자 ID
     * @param postId 수정할 게시글 ID
     * @param updateRequest 수정된 게시글 정보
     */
    public void updatePost(long requestUserId, long postId, @Valid UpdatePostRequest updateRequest) {
        PostEntity oldPostEntity = getById(postId);

        if (requestUserId != oldPostEntity.getUserId()) {
            throw new InsufficientAuthorizationException(
                    "게시글을 소유한 사용자만 수행할 수 있습니다. requestUserId: " + requestUserId + ", postId: " + postId);
        }

        PostEntity newPostEntity = PostEntity.builder()
            .id(oldPostEntity.getId())
            .title(updateRequest.title())
            .content(updateRequest.content())
            .readStatus(updateRequest.readStatus())
            .userId(oldPostEntity.getUserId())
            .build();

        this.postRepository.save(newPostEntity);
    }

    /**
     * 게시글을 삭제합니다. 게시글의 댓글도 함꼐 삭제됩니다. 어드민 혹은 글을 소유한 사용자만 수행할 수 있습니다.
     * @param requestUserId 게시글을 삭제하려는 사용자 ID
     * @param postId 삭제할 게시글 ID
     */
    @Transactional
    public void deletePost(long requestUserId, long postId) {
        PostEntity postEntity = getById(postId);
        validUserIsSelfOrAdmin(requestUserId, postEntity.getUserId());
        this.replyService.deleteAllByPostId(postId);
        this.postRepository.delete(postEntity);
    }

    /**
     * 게시글 정보를 조회합니다. 공개되지 않은 글은 어드민이나 본인만 확인 가능합니다.
     * @param requestUserId 게시글을 조회하려는 사용자 ID
     * @param postId 조회할 게시글 ID
     * @return 게시글 DTO
     */
    public PostDto getPost(long requestUserId, long postId) {
        PostEntity postEntity = getById(postId);
        validUserId(requestUserId);

        if (isUserAuthorized(requestUserId, postEntity)) {
            throw new InsufficientAuthorizationException(
                    "공개되지 않은 글은 어드민이나 본인만 확인 가능합니다. requestUserId: " + requestUserId + ", postId: " + postId);
        }
        return postEntity.mapToPostDto();
    }

    /**
     * 해당 게시글에 접근 가능한 유저인지 확인합니다.
     * @param requestUserId 게시글을 조회하려는 사용자 ID
     * @param postId 조회할 게시글 ID
     */
    public void validPost(long requestUserId, long postId) {
        PostEntity postEntity = getById(postId);
        validUserId(requestUserId);

        if (isUserAuthorized(requestUserId, postEntity)) {
            throw new InsufficientAuthorizationException(
                    "공개되지 않은 글은 어드민이나 본인만 확인 가능합니다. requestUserId: " + requestUserId + ", postId: " + postId);
        }
    }

    /**
     * 공개된 모든 요약 게시글을 가져옵니다.
     * @return 공개된 요약 게시글 리스트
     */
    public List<PostSummaryDto> getAllPublishedPosts() {
        return this.postRepository.findAll()
            .stream()
            .filter(this::isPublished)
            .map(PostEntity::mapToPostSummaryDto)
            .collect(Collectors.toList());
    }

    /**
     * 특정 사용자의 공개된 상태의 모든 요약 게시글을 가져옵니다.
     * @param userId 조회하고자 하는 사용자 ID
     * @return 특정 사용자의 공개된 요약 게시글 리스트
     */
    public List<PostSummaryDto> getAllPublishedPostsByUserId(long userId) {
        return this.postRepository.findAllByUserId(userId)
            .stream()
            .filter(this::isPublished)
            .map(PostEntity::mapToPostSummaryDto)
            .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 작성한 모든 요약 게시글(공개 및 임시 저장)을 가져옵니다. 본인 혹은 어드민만 조회 가능합니다.
     * @param requestUserId 요청하는 사용자 ID
     * @param userId 조회하고자 하는 사용자 ID
     * @return 사용자가 작성한 요약 게시글 리스트
     */
    public List<PostSummaryDto> getAllPostsByUserId(long requestUserId, long userId) {
        validUserIsSelfOrAdmin(requestUserId, userId);
        return this.postRepository.findAllByUserId(userId)
            .stream()
            .map(PostEntity::mapToPostSummaryDto)
            .collect(Collectors.toList());
    }

    /**
     * 게시글이 존재하는지 검사합니다.
     * @param postId 조회할 게시글 ID
     */
    public void validPostById(long postId) {
        if (!this.postRepository.existsById(postId)) {
            throw new NotFoundResourceException("ID에 해당되는 ReplyEntity를 찾을 수 없습니다. postId: " + postId);
        }
    }

    /**
     * 게시글이 공개된 상태인지 확인합니다.
     * @param post 게시글 엔티티
     * @return 게시글이 공개된 상태라면 True, 공개되지 않았다면 False 반환
     */
    private boolean isPublished(PostEntity post) {
        return post.getReadStatus().equals(PublicationStatus.PUBLIC_PUBLISHED);
    }

    private boolean isUserAuthorized(long requestUserId, PostEntity postEntity) {
        boolean isOwnPost = requestUserId == postEntity.getUserId();
        boolean isPublishedPost = isPublished(postEntity);
        boolean isAdminUser = this.userService.getUserById(requestUserId).getRole().equals(Role.ADMIN);
        return !isOwnPost && !isPublishedPost && !isAdminUser;
    }

    private PostEntity getById(long postId) {
        return this.postRepository.findById(postId)
            .orElseThrow(() -> new NotFoundResourceException("ID에 해당되는 ReplyEntity를 찾을 수 없습니다. postId: " + postId));
    }

    private void validUserId(long userId) {
        this.userService.validUserById(userId);
    }

    private void validUserIsSelfOrAdmin(long requestUserId, long userId) {
        this.userService.validUserIsSelfOrAdmin(requestUserId, userId);
    }

}
