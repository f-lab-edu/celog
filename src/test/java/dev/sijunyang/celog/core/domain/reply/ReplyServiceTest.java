package dev.sijunyang.celog.core.domain.reply;

import dev.sijunyang.celog.core.domain.post.PostDto;
import dev.sijunyang.celog.core.domain.post.PostService;
import dev.sijunyang.celog.core.domain.user.RequestUser;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.enums.PublicationStatus;
import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.error.nextVer.InsufficientPermissionException;
import dev.sijunyang.celog.core.global.error.nextVer.ResourceNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReplyService replyService;

    @Captor
    private ArgumentCaptor<ReplyEntity> replyEntity;

    @Test
    void shouldCreateNewReply() {
        // Given
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.USER);
        long postId = 1L;

        String content = "This is a test reply.";
        Long superReplyId = null;
        CreateReplyRequest createReplyRequest = new CreateReplyRequest(content, postId, superReplyId);

        doNothing().when(this.userService).validateUserExistence(requestUserId);
        doNothing().when(this.postService).validateUserPostAccess(requester, postId);
        when(this.replyRepository.save(any())).thenReturn(null); // 반환 값 사용하지 않음

        // When
        this.replyService.createReply(requester, createReplyRequest);

        // Then
        verify(this.replyRepository, times(1)).save(this.replyEntity.capture());
        ReplyEntity capturedEntity = this.replyEntity.getValue();
        Assertions.assertEquals(content, capturedEntity.getContent());
        Assertions.assertEquals(postId, capturedEntity.getPostId());
        Assertions.assertEquals(superReplyId, capturedEntity.getSuperReplyId());
    }

    @Test
    void shouldUpdateExistingReply() {
        // Given
        long replyId = 1L;
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.USER);
        long postId = 1L;
        Long superReplyId = null;

        String content = "This is a updated reply.";
        UpdateReplyRequest updateReplyRequest = new UpdateReplyRequest(content);
        ReplyEntity exisitngReplyEntity = ReplyEntity.builder()
            .id(replyId)
            .content(content)
            .userId(requestUserId)
            .postId(postId)
            .superReplyId(superReplyId)
            .build();

        doNothing().when(this.userService).validateUserExistence(requestUserId);
        when(this.replyRepository.findById(replyId)).thenReturn(Optional.of(exisitngReplyEntity));
        doNothing().when(this.postService).validateUserPostAccess(requester, postId);

        when(this.replyRepository.save(any())).thenReturn(null); // 반환 값 사용하지 않음

        // When
        this.replyService.updateReply(requester, replyId, updateReplyRequest);

        // Then
        verify(this.replyRepository, times(1)).save(this.replyEntity.capture());
        ReplyEntity capturedEntity = this.replyEntity.getValue();
        Assertions.assertEquals(content, capturedEntity.getContent());
        Assertions.assertEquals(postId, capturedEntity.getPostId());
        Assertions.assertEquals(superReplyId, capturedEntity.getSuperReplyId());
    }

    @Test
    void shouldDeleteExistingReply() {
        // Given
        long replyId = 1L;
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.USER);
        long postId = 1L;
        ReplyEntity existingReplyEntity = ReplyEntity.builder()
            .id(replyId)
            .postId(postId)
            .userId(requestUserId)
            .build();

        doNothing().when(this.userService).validateUserExistence(requestUserId);
        when(this.replyRepository.findById(replyId)).thenReturn(Optional.of(existingReplyEntity));
        doNothing().when(this.postService).validatePostById(postId);
        doNothing().when(this.replyRepository).delete(any());

        // When
        this.replyService.deleteReply(requester, replyId);

        // Then
        verify(this.replyRepository, times(1)).delete(existingReplyEntity);
        verify(this.replyRepository, times(1)).findById(replyId);
        verify(this.postService, times(1)).validatePostById(postId);
    }

    @Test
    void shouldDeleteAllReplyByPost() {
        // Given
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.USER);
        long postId = 1L;
        long userId = 1L;
        PostDto existingPostDto = new PostDto(postId, "title", "content", PublicationStatus.PUBLIC_PUBLISHED, userId,
                null, null);

        doNothing().when(this.userService).validateUserExistence(requestUserId);
        when(this.postService.getPost(requester, postId)).thenReturn(existingPostDto);
        when(this.replyRepository.deleteAllByPostId(postId)).thenReturn(null); // 반환 값
                                                                               // 사용하지 않음

        // When
        this.replyService.deleteAllByPostId(requester, postId);

        // Then
        verify(this.replyRepository, times(1)).deleteAllByPostId(postId);
        verify(this.postService, times(1)).getPost(requester, postId);
    }

    @Test
    void shouldReturnRootReplies() {
        // Given
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.USER);
        long postId = 1L;
        long user1Id = 1L;
        long user2Id = 2L;
        long reply1Id = 1L;
        long reply2Id = 2L;
        List<ReplyEntity> zeroDepthReplies = List.of(
                ReplyEntity.builder().id(reply1Id).postId(postId).userId(user1Id).superReplyId(null).build(),
                ReplyEntity.builder().id(reply2Id).postId(postId).userId(user2Id).superReplyId(null).build());

        doNothing().when(this.userService).validateUserExistence(requestUserId);
        doNothing().when(this.postService).validateUserPostAccess(requester, postId);
        when(this.replyRepository.findAllByPostIdAndSuperReplyIdIsNull(postId)).thenReturn(zeroDepthReplies);

        // When
        List<ReplyDto> rt = this.replyService.getRootRepliesByPostId(requester, postId);

        // Then
        rt.forEach((replyDto) -> assertNull(replyDto.getSuperReplyId()));
    }

    @Test
    void shouldReturnChildRepliesById() {
        // Given
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.USER);
        long postId = 1L;
        long user1Id = 1L;
        long user2Id = 2L;
        long superReplyId = 1L;
        long reply1Id = 1L;
        long reply2Id = 2L;
        ReplyEntity superReply = ReplyEntity.builder()
            .id(superReplyId)
            .postId(postId)
            .userId(user1Id)
            .superReplyId(null)
            .build();
        List<ReplyEntity> zeroDepthReplies = List.of(
                ReplyEntity.builder().id(reply1Id).postId(postId).userId(user1Id).superReplyId(superReplyId).build(),
                ReplyEntity.builder().id(reply2Id).postId(postId).userId(user2Id).superReplyId(superReplyId).build());

        doNothing().when(this.userService).validateUserExistence(requestUserId);
        when(this.replyRepository.findById(superReplyId)).thenReturn(Optional.of(superReply));
        doNothing().when(this.postService).validateUserPostAccess(requester, postId);
        when(this.replyRepository.findAllBySuperReplyId(postId)).thenReturn(zeroDepthReplies);

        // When
        List<ReplyDto> rt = this.replyService.getChildRepliesById(requester, postId);

        // Then
        rt.forEach((replyDto) -> assertEquals(superReplyId, replyDto.getSuperReplyId()));
    }

    @Test
    void shouldThrowExceptionWhenReplyNotExist() {
        // Given
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.USER);
        long replyId = 1L;
        UpdateReplyRequest updateReplyRequest = new UpdateReplyRequest("content");

        doNothing().when(this.userService).validateUserExistence(requestUserId);
        when(this.replyRepository.findById(replyId)).thenReturn(Optional.empty());
        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> this.replyService.updateReply(requester, replyId, updateReplyRequest));
    }

    @Test
    void shouldThrowExceptionWhenAnotherUserUpdateReply() {
        // Given
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.USER);
        long anotherUserId = 2L;
        long replyId = 1L;
        long postId = 2L;
        ReplyEntity exisitngReplyEntity = ReplyEntity.builder()
            .id(replyId)
            .postId(postId)
            .userId(anotherUserId)
            .build();
        UpdateReplyRequest updateReplyRequest = new UpdateReplyRequest("content");

        doNothing().when(this.userService).validateUserExistence(requestUserId);
        when(this.replyRepository.findById(replyId)).thenReturn(Optional.of(exisitngReplyEntity));
        doNothing().when(this.postService).validateUserPostAccess(requester, postId);

        // When & Then
        assertThrows(InsufficientPermissionException.class,
                () -> this.replyService.updateReply(requester, replyId, updateReplyRequest));
    }

}
