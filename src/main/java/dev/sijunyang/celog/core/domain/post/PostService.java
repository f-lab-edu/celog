package dev.sijunyang.celog.core.domain.post;

import java.util.List;
import java.util.stream.Collectors;

import dev.sijunyang.celog.core.domain.reply.ReplyService;
import dev.sijunyang.celog.core.domain.user.RequestUser;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.enums.PublicationStatus;
import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.error.nextVer.InsufficientPermissionException;
import dev.sijunyang.celog.core.global.error.nextVer.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
     * @param requester 게시글을 작성할 사용자 정보
     * @param createPostRequest 생성할 게시글 정보
     */
    public void createPost(@NotNull @Valid RequestUser requester, @NotNull @Valid CreatePostRequest createPostRequest) {
        validateUserById(requester.userId());
        PostEntity postEntity = PostEntity.builder()
            .id(null)
            .title(createPostRequest.title())
            .content(createPostRequest.content())
            .readStatus(createPostRequest.readStatus())
            .userId(requester.userId())
            .build();
        this.postRepository.save(postEntity);
    }

    /**
     * 기존 게시글 정보를 수정합니다. 게시글을 소유한 사용자만 수행할 수 있습니다.
     * @param requester 게시글을 수정하려는 사용자 정보
     * @param postId 수정할 게시글 ID
     * @param updateRequest 수정된 게시글 정보
     */
    public void updatePost(@NotNull @Valid RequestUser requester, long postId,
            @NotNull @Valid UpdatePostRequest updateRequest) {
        validateUserById(requester.userId());
        PostEntity oldPostEntity = getById(postId);

        if (requester.userId() != oldPostEntity.getUserId()) {
            throw new InsufficientPermissionException(
                    "게시글을 소유한 사용자만 수행할 수 있습니다. requestUserId: " + requester.userId() + ", postId: " + postId);
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
     * @param requester 게시글을 삭제하려는 사용자 정보
     * @param postId 삭제할 게시글 ID
     */
    @Transactional
    public void deletePost(@NotNull @Valid RequestUser requester, long postId) {
        validateUserById(requester.userId());
        PostEntity postEntity = getById(postId);
        validateUserPostAccess(requester, postId);
        this.replyService.deleteAllByPostId(requester, postId);
        this.postRepository.delete(postEntity);
    }

    /**
     * 게시글 정보를 조회합니다. 공개되지 않은 글은 어드민이나 본인만 확인 가능합니다.
     * @param requester 게시글을 조회하려는 사용자 정보
     * @param postId 조회할 게시글 ID
     * @return 게시글 DTO
     */
    public PostDto getPost(@NotNull @Valid RequestUser requester, long postId) {
        validateUserById(requester.userId());
        validateUserPostAccess(requester, postId);

        PostEntity postEntity = getById(postId);
        return postEntity.mapToPostDto();
    }

    /**
     * 해당 게시글에 접근 가능한 유저인지 확인합니다.
     * @param requester 게시글을 조회하려는 사용자 정보
     * @param postId 조회할 게시글 ID
     */
    public void validateUserPostAccess(@NotNull @Valid RequestUser requester, long postId) {
        validateUserById(requester.userId());
        PostEntity postEntity = getById(postId);

        if (!isPostAccessible(requester, postEntity)) {
            throw new InsufficientPermissionException(
                    "공개되지 않은 글은 어드민이나 본인만 확인 가능합니다. requestUserId: " + requester.userId() + ", postId: " + postId);
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
     * @param requester 요청하는 사용자 정보
     * @param userId 조회하고자 하는 사용자 ID
     * @return 사용자가 작성한 요약 게시글 리스트
     */
    public List<PostSummaryDto> getAllPostsByUserId(@NotNull @Valid RequestUser requester, long userId) {
        validateUserById(userId);
        validateUserById(requester.userId());
        validateUserIsSelfOrAdmin(requester, userId);
        return this.postRepository.findAllByUserId(userId)
            .stream()
            .map(PostEntity::mapToPostSummaryDto)
            .collect(Collectors.toList());
    }

    /**
     * 게시글이 존재하는지 검사합니다.
     * @param postId 조회할 게시글 ID
     */
    public void validatePostById(long postId) {
        if (!this.postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("ID에 해당되는 PostEntity를 찾을 수 없습니다. postId: " + postId);
        }
    }

    /**
     * 게시글이 공개된 상태인지 확인합니다.
     * @param post 게시글 엔티티
     * @return 게시글이 공개된 상태라면 True, 공개되지 않았다면 False 반환
     */
    private boolean isPublished(@NotNull PostEntity post) {
        return post.getReadStatus().equals(PublicationStatus.PUBLIC_PUBLISHED);
    }

    private boolean isPostAccessible(@NotNull @Valid RequestUser requester, @NotNull PostEntity postEntity) {
        // when admin
        if (requester.userRole().equals(Role.ADMIN)) {
            return true;
        }
        // when owner
        if (requester.userId() == postEntity.getUserId()) {
            return true;
        }
        // when published
        if (isPublished(postEntity)) {
            return true;
        }
        return false;
    }

    private PostEntity getById(long postId) {
        return this.postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("ID에 해당되는 PostEntity를 찾을 수 없습니다. postId: " + postId));
    }

    private void validateUserById(long userId) {
        this.userService.validUserById(userId);
    }

    private void validateUserIsSelfOrAdmin(RequestUser requester, long userId) {
        if (requester.userId() == userId || requester.userRole().equals(Role.ADMIN)) {
            return;
        }
        throw new InsufficientPermissionException(
                "어드민이나 본인만 요청을 수행할 수 있습니다. requester: " + requester + "userId: " + userId);
    }

}
