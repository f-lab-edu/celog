package dev.sijunyang.celog.core.domain.reply;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.sijunyang.celog.core.domain.post.PostService;
import dev.sijunyang.celog.core.domain.user.UserService;

import dev.sijunyang.celog.core.global.error.nextVer.InsufficientAuthorizationException;
import dev.sijunyang.celog.core.global.error.nextVer.NotFoundResourceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final PostService postService;

    private final UserService userService;

    /**
     * 새로운 댓글을 생성합니다. 댓글을 생성하려는 게시글에 접근 가능해야 합니다.
     * @param requestUserId 댓글을 생성하려는 사용자 ID
     * @param createReplyRequest 생성할 댓글 정보
     */
    public void createReply(long requestUserId, @Valid CreateReplyRequest createReplyRequest) {
        validPostAccessible(requestUserId, createReplyRequest.postId());
        ReplyEntity replyEntity = ReplyEntity.builder()
            .id(null)
            .content(createReplyRequest.content())
            .userId(requestUserId)
            .postId(createReplyRequest.postId())
            .superReplyId(createReplyRequest.superReplyId())
            .build();
        this.replyRepository.save(replyEntity);
    }

    /**
     * 기존 댓글 정보를 수정합니다. 수정하려는 댓글이 달린 게시글에 접근 가능한 사용자이며, 작성자 본인만 수행 가능합니다.
     * @param requestUserId 댓글을 수정하려는 사용자 ID
     * @param replyId 수정할 댓글 ID
     * @param updateRequest 수정된 댓글 정보
     */
    public void updateReply(long requestUserId, long replyId, @Valid UpdateReplyRequest updateRequest) {
        ReplyEntity oldReplyEntity = getById(replyId);
        validPostAccessible(requestUserId, oldReplyEntity.getPostId());
        validUpdatable(requestUserId, oldReplyEntity);

        ReplyEntity newReplyEntity = ReplyEntity.builder()
            .id(oldReplyEntity.getId())
            .content(updateRequest.content())
            .userId(requestUserId)
            .postId(oldReplyEntity.getPostId())
            .superReplyId(oldReplyEntity.getSuperReplyId())
            .build();

        this.replyRepository.save(newReplyEntity);
    }

    /**
     * 댓글을 삭제합니다. 어드민 혹은 작성자 본인만 수행 가능합니다.
     * @param requestUserId 댓글을 삭제하려는 사용자 ID
     * @param replyId 삭제할 댓글 ID
     */
    public void deleteReply(long requestUserId, long replyId) {
        ReplyEntity replyEntity = getById(replyId);
        // Post에 접근 불가능해도, 이미 작성한 글은 제거 가능해야 하므로 Post 접근 권한 검사는 하지 않는다. 실제 존재하는지만 확인.
        this.postService.validPostById(replyEntity.getPostId());
        validDeletable(requestUserId, replyEntity);
        this.replyRepository.delete(replyEntity);
    }

    /**
     * 특정 게시글의 모든 댓글을 삭제합니다. 사용자의 요청이 아닌 게시글이 삭제될 때 실행됩니다.
     * @param postId 삭제할 댓글 ID
     */
    public void deleteAllByPostId(long postId) {
        this.replyRepository.deleteAllByPostId(postId);
    }

    /**
     * 특정 게시글에 달린 depth가 0인(글에 대한 댓글) 댓글을 가져옵니다.
     * @param requestUserId 댓글을 조회하려는 사용자 ID
     * @param postId 조회할 게시글 ID
     * @return 해당 게시글에 달린 댓글 리스트
     */
    public List<ReplyDto> getAllZeroDepthByPostId(long requestUserId, long postId) {
        validPostAccessible(requestUserId, postId);
        List<ReplyEntity> rootReplies = this.replyRepository.findAllByPostIdAndSuperReplyIdIsNull(postId);
        return rootReplies.stream().map(ReplyEntity::tooReplyDto).toList();
    }

    /**
     * 특정 댓글에 대한 댓글을 가져옵니다.
     * @param requestUserId 댓글을 조회하려는 사용자 ID
     * @param superReplyId 조회할 댓글 ID
     * @return 해당 댓글에 달린 댓글 리스트
     */
    public List<ReplyDto> getAllBySuperId(long requestUserId, long superReplyId) {
        ReplyEntity replyEntity = getById(superReplyId);
        validPostAccessible(requestUserId, replyEntity.getPostId());
        List<ReplyEntity> childReplies = this.replyRepository.findAllBySuperReplyId(superReplyId);
        return childReplies.stream().map(ReplyEntity::tooReplyDto).collect(Collectors.toList());
    }

    private ReplyEntity getById(long replyId) {
        return this.replyRepository.findById(replyId)
            .orElseThrow(() -> new NotFoundResourceException("ID에 해당되는 ReplyEntity를 찾을 수 없습니다. replyId: " + replyId));
    }

    private void validUpdatable(long requestUserId, ReplyEntity entity) {
        if (!entity.getUserId().equals(requestUserId)) {
            throw new InsufficientAuthorizationException(
                    "작성자 본인만 댓글을 수정할 수 있습니다. requestUserId: " + requestUserId + ", replyId: " + entity.getId());
        }
    }

    private void validDeletable(long requestUserId, ReplyEntity entity) {
        this.userService.validUserIsSelfOrAdmin(requestUserId, entity.getUserId());
    }

    private void validPostAccessible(long requestUserId, long postId) {
        this.postService.validPost(requestUserId, postId);
    }

}
