package dev.sijunyang.celog.api;

import java.util.List;

import dev.sijunyang.celog.core.domain.reply.CreateReplyRequest;
import dev.sijunyang.celog.core.domain.reply.ReplyDto;
import dev.sijunyang.celog.core.domain.reply.ReplyService;
import dev.sijunyang.celog.core.domain.reply.UpdateReplyRequest;
import dev.sijunyang.celog.core.domain.user.RequestUser;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/posts/{postId}/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final AuthenticatedUserManager authenticatedUserManager;

    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<ReplyDto> createReply(@PathVariable Long postId, @RequestBody CreateReplyRequest request) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        this.replyService.createReply(requestUser, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{replyId}")
    public ResponseEntity<ReplyDto> updateReply(@PathVariable Long postId, @PathVariable Long replyId,
            @RequestBody UpdateReplyRequest request) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        this.replyService.updateReply(requestUser, postId, replyId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long postId, @PathVariable Long replyId) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        this.replyService.deleteReply(requestUser, postId, replyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{replyId}/children")
    public ResponseEntity<List<ReplyDto>> getChildReplies(@PathVariable Long postId, @PathVariable Long replyId) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        List<ReplyDto> replies = this.replyService.getChildRepliesById(requestUser, postId, replyId);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/roots")
    public ResponseEntity<List<ReplyDto>> getRootReplies(@PathVariable Long postId) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        List<ReplyDto> replies = this.replyService.getRootRepliesByPostId(requestUser, postId);
        return ResponseEntity.ok(replies);
    }

}
