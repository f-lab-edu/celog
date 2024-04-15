package dev.sijunyang.celog.core.domain.reply;

import java.util.List;
import java.util.stream.Collectors;

import dev.sijunyang.celog.core.domain.post.PostDto;
import dev.sijunyang.celog.core.domain.post.PostService;
import dev.sijunyang.celog.core.domain.user.RequestUser;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.error.nextVer.InsufficientPermissionException;
import dev.sijunyang.celog.core.global.error.nextVer.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final PostService postService;

    private final UserService userService;

    public ReplyService(ReplyRepository replyRepository, @Lazy PostService postService, UserService userService) {
        this.replyRepository = replyRepository;
        this.postService = postService;
        this.userService = userService;
    }

    /**
     * 새로운 댓글을 생성합니다. 댓글을 생성하려는 게시글에 접근 가능해야 합니다.
     * @param requester 댓글을 생성하려는 사용자 정보
     * @param postId 수정할 댓글의 게시글 ID
     * @param createReplyRequest 생성할 댓글 정보
     */
    public void createReply(@NotNull @Valid RequestUser requester, long postId,
            @NotNull @Valid CreateReplyRequest createReplyRequest) {
        validateUserById(requester.userId());
        validatePostAccessible(requester, postId);
        ReplyEntity replyEntity = ReplyEntity.builder()
            .id(null)
            .content(createReplyRequest.content())
            .userId(requester.userId())
            .postId(postId)
            .superReplyId(createReplyRequest.superReplyId())
            .build();
        this.replyRepository.save(replyEntity);
    }

    /**
     * 기존 댓글 정보를 수정합니다. 수정하려는 댓글이 달린 게시글에 접근 가능한 사용자이며, 작성자 본인만 수행 가능합니다.
     * @param requester 댓글을 수정하려는 사용자 정보
     * @param postId 수정할 댓글의 게시글 ID
     * @param replyId 수정할 댓글 ID
     * @param updateRequest 수정된 댓글 정보
     */
    public void updateReply(@NotNull @Valid RequestUser requester, long postId, long replyId,
            @NotNull @Valid UpdateReplyRequest updateRequest) {
        validateUserById(requester.userId());
        ReplyEntity oldReplyEntity = getById(replyId);
        validatePostHasReply(postId, oldReplyEntity);
        validatePostAccessible(requester, oldReplyEntity.getPostId());
        validateUpdatable(requester, oldReplyEntity);

        ReplyEntity newReplyEntity = ReplyEntity.builder()
            .id(oldReplyEntity.getId())
            .content(updateRequest.content())
            .userId(requester.userId())
            .postId(oldReplyEntity.getPostId())
            .superReplyId(oldReplyEntity.getSuperReplyId())
            .build();

        this.replyRepository.save(newReplyEntity);
    }

    /**
     * 댓글을 삭제합니다. 어드민 혹은 작성자 본인만 수행 가능합니다.
     * @param requester 댓글을 삭제하려는 사용자 정보
     * @param postId 수정할 댓글의 게시글 ID
     * @param replyId 삭제할 댓글 ID
     */
    public void deleteReply(@NotNull @Valid RequestUser requester, long postId, long replyId) {
        validateUserById(requester.userId());
        ReplyEntity replyEntity = getById(replyId);
        validatePostHasReply(postId, replyEntity);
        validateDeletable(requester, replyEntity.getUserId());
        this.replyRepository.delete(replyEntity);
    }

    /**
     * 특정 게시글의 모든 댓글을 삭제합니다. 사용자의 요청이 아닌 게시글이 삭제될 때 실행됩니다.
     * @param requester 글과 함께 모든 댓글을 삭제하려는 사용자 정보
     * @param postId 삭제할 게시글 ID
     */
    public void deleteAllByPostId(@NotNull @Valid RequestUser requester, long postId) {
        validateUserById(requester.userId());
        PostDto post = this.postService.getPost(requester, postId);
        validateDeletable(requester, post.userId());
        this.replyRepository.deleteAllByPostId(postId);
    }

    /**
     * 특정 게시글에 달린 depth가 0인(글에 대한 댓글) 댓글을 가져옵니다.
     * @param requester 댓글을 조회하려는 사용자 정보
     * @param postId 조회할 게시글 ID
     * @return 해당 게시글에 달린 댓글 리스트
     */
    public List<ReplyDto> getRootRepliesByPostId(@NotNull @Valid RequestUser requester, long postId) {
        validateUserById(requester.userId());
        validatePostAccessible(requester, postId);
        List<ReplyEntity> rootReplies = this.replyRepository.findAllByPostIdAndSuperReplyIdIsNull(postId);
        return rootReplies.stream().map(ReplyEntity::tooReplyDto).toList();
    }

    /**
     * 특정 댓글에 대한 댓글을 가져옵니다.
     * @param requester 댓글을 조회하려는 사용자 정보
     * @param postId 수정할 댓글의 게시글 ID
     * @param parentReplyId 조회할 댓글 ID
     * @return 해당 댓글에 달린 댓글 리스트
     */
    public List<ReplyDto> getChildRepliesById(@NotNull @Valid RequestUser requester, long postId, long parentReplyId) {
        validateUserById(requester.userId());
        ReplyEntity parentReplyEntity = getById(parentReplyId);
        validatePostHasReply(postId, parentReplyEntity);
        validatePostAccessible(requester, parentReplyEntity.getPostId());
        List<ReplyEntity> childReplies = this.replyRepository.findAllBySuperReplyId(parentReplyId);
        return childReplies.stream().map(ReplyEntity::tooReplyDto).collect(Collectors.toList());
    }

    // TODO 여기서 ReplyEntity 가져올 때부터 postId 검사하는게 더 좋을듯?
    private ReplyEntity getById(long replyId) {
        return this.replyRepository.findById(replyId)
            .orElseThrow(() -> new ResourceNotFoundException("ID에 해당되는 ReplyEntity를 찾을 수 없습니다. replyId: " + replyId));
    }

    private void validateUpdatable(RequestUser requester, ReplyEntity entity) {
        if (!entity.getUserId().equals(requester.userId())) {
            throw new InsufficientPermissionException(
                    "작성자 본인만 댓글을 수정할 수 있습니다. requestUserId: " + requester.userId() + ", replyId: " + entity.getId());
        }
    }

    private void validateDeletable(RequestUser requester, long userId) {
        if (requester.userId() == userId || requester.userRole().equals(Role.ADMIN)) {
            return;
        }
        throw new InsufficientPermissionException(
                "어드민이나 본인만 요청을 수행할 수 있습니다. requester: " + requester + "userId: " + userId);
    }

    private void validatePostAccessible(RequestUser requester, long postId) {
        this.postService.validateUserPostAccess(requester, postId);
    }

    private void validateUserById(long userId) {
        this.userService.validateUserExistence(userId);
    }

    private void validatePostHasReply(long postId, ReplyEntity reply) {
        this.postService.validatePostById(postId);
        if (!reply.getPostId().equals(postId)) {
            throw new ResourceNotFoundException(
                    "reply의 post와 postId가 일치하지 않습니다. postId: " + postId + ", replyId: " + reply.getId());
        }
    }

}
