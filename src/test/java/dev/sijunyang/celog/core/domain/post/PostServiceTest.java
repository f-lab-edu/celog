package dev.sijunyang.celog.core.domain.post;

import java.util.List;
import java.util.Optional;

import dev.sijunyang.celog.core.domain.reply.ReplyService;
import dev.sijunyang.celog.core.domain.user.UserDto;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.RequestUser;
import dev.sijunyang.celog.core.global.enums.PublicationStatus;
import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.error.nextVer.InsufficientPermissionException;
import dev.sijunyang.celog.core.global.error.nextVer.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private ReplyService replyService;

    @InjectMocks
    private PostService postService;

    @Captor
    private ArgumentCaptor<PostEntity> postEntityCaptor;

    @Test
    void shouldCreateNewPost() {
        // Given
        long userId = 1L;
        RequestUser requester = new RequestUser(userId, Role.USER);
        CreatePostRequest createPostRequest = new CreatePostRequest("Test Post", "This is a test post.",
                PublicationStatus.DRAFTING);

        doNothing().when(this.userService).validUserById(userId);
        when(this.postRepository.save(any())).thenReturn(null); // 반환 값을 사용하지 않음

        // When
        this.postService.createPost(requester, createPostRequest);

        // Then
        verify(this.postRepository, times(1)).save(this.postEntityCaptor.capture());
        PostEntity capturedEntity = this.postEntityCaptor.getValue();
        assertEquals(createPostRequest.title(), capturedEntity.getTitle());
        assertEquals(createPostRequest.content(), capturedEntity.getContent());
        assertEquals(createPostRequest.readStatus(), capturedEntity.getReadStatus());
        assertEquals(userId, capturedEntity.getUserId());
    }

    @Test
    void shouldUpdateExistingPost() {
        // Given
        long userId = 1L;
        RequestUser requester = new RequestUser(userId, Role.USER);
        long postId = 1L;
        UpdatePostRequest updateRequest = new UpdatePostRequest("Updated Title", "Updated Content",
                PublicationStatus.PUBLIC_PUBLISHED);
        PostEntity existingPostEntity = PostEntity.builder()
            .id(postId)
            .title("Old Title")
            .content("Old Content")
            .readStatus(PublicationStatus.DRAFTING)
            .userId(userId)
            .build();

        when(this.postRepository.findById(postId)).thenReturn(Optional.of(existingPostEntity));
        when(this.postRepository.save(any())).thenReturn(null); // 반환 값을 사용하지 않음

        // When
        this.postService.updatePost(requester, postId, updateRequest);

        // Then
        verify(this.postRepository, times(1)).save(this.postEntityCaptor.capture());
        PostEntity capturedEntity = this.postEntityCaptor.getValue();
        assertEquals(postId, capturedEntity.getId());
        assertEquals(updateRequest.title(), capturedEntity.getTitle());
        assertEquals(updateRequest.content(), capturedEntity.getContent());
        assertEquals(updateRequest.readStatus(), capturedEntity.getReadStatus());
        assertEquals(userId, capturedEntity.getUserId());
    }

    @Test
    void shouldDeleteExistingPost() {
        // Given
        long requestUserId = 1L;
        long userId = 1L;
        RequestUser requester = new RequestUser(userId, Role.USER);
        long postId = 1L;
        PostEntity existingPostEntity = PostEntity.builder().id(postId).userId(userId).build();

        when(this.postRepository.findById(postId)).thenReturn(Optional.of(existingPostEntity));
        doNothing().when(this.replyService).deleteAllByPostId(requestUserId, postId);

        // When
        this.postService.deletePost(requester, postId);

        // Then
        verify(this.postRepository, times(1)).delete(existingPostEntity);
    }

    @Test
    void shouldReturnExistingPost() {
        // Given
        long userId = 1L;
        RequestUser requester = new RequestUser(userId, Role.USER);
        long postId = 1L;
        PostEntity existingPostEntity = PostEntity.builder()
            .id(postId)
            .title("Test Post")
            .content("This is a test post.")
            .readStatus(PublicationStatus.PUBLIC_PUBLISHED)
            .userId(userId)
            .build();
        UserDto user = UserDto.builder().id(userId).role(Role.USER).build();

        when(this.postRepository.findById(postId)).thenReturn(Optional.of(existingPostEntity));
        doNothing().when(this.userService).validUserById(userId);

        // When
        PostDto retrievedPostDto = this.postService.getPost(requester, postId);

        // Then
        assertNotNull(retrievedPostDto);
        assertEquals(existingPostEntity.getId(), retrievedPostDto.postId());
        assertEquals(existingPostEntity.getTitle(), retrievedPostDto.title());
        assertEquals(existingPostEntity.getContent(), retrievedPostDto.content());
        assertEquals(existingPostEntity.getReadStatus(), retrievedPostDto.readStatus());
        assertEquals(existingPostEntity.getUserId(), retrievedPostDto.userId());
    }

    @Test
    void shouldThrowExceptionWhenUserCannotAccessPost() {
        // Given
        long postId = 1L;
        long requestUserId = 1L;
        long otherUserId = 2L;
        RequestUser otherUserRequester = new RequestUser(otherUserId, Role.USER);
        PostEntity draftingPostEntity = PostEntity.builder()
            .id(postId)
            .title("Test Post")
            .content("This is a test post.")
            .readStatus(PublicationStatus.DRAFTING)
            .userId(requestUserId)
            .build();

        when(this.postRepository.findById(postId)).thenReturn(Optional.of(draftingPostEntity));
        doNothing().when(this.userService).validUserById(otherUserId);

        // When & Then
        assertThrows(InsufficientPermissionException.class, () -> this.postService.getPost(otherUserRequester, postId));
    }

    @Test
    void shouldReturnAllPublishedPosts() {
        // Given
        long userId = 1L;
        long otherUserId = 2L;
        PostEntity publishedPost1 = PostEntity.builder()
            .id(1L)
            .title("Published Post 1")
            .content("This is a published post.")
            .readStatus(PublicationStatus.PUBLIC_PUBLISHED)
            .userId(userId)
            .build();
        PostEntity draftPost = PostEntity.builder()
            .id(2L)
            .title("Draft Post")
            .content("This is a draft post.")
            .readStatus(PublicationStatus.DRAFTING)
            .userId(userId)
            .build();
        PostEntity publishedPost2 = PostEntity.builder()
            .id(3L)
            .title("Published Post 2")
            .content("This is another published post.")
            .readStatus(PublicationStatus.PUBLIC_PUBLISHED)
            .userId(otherUserId)
            .build();

        when(this.postRepository.findAll()).thenReturn(List.of(publishedPost1, draftPost, publishedPost2));

        // When
        List<PostSummaryDto> publishedPosts = this.postService.getAllPublishedPosts();

        // Then
        assertEquals(2, publishedPosts.size());
        assertTrue(publishedPosts.stream().anyMatch((p) -> p.postId().equals(publishedPost1.getId())));
        assertTrue(publishedPosts.stream().anyMatch((p) -> p.postId().equals(publishedPost2.getId())));
    }

    @Test
    void shouldReturnAllPostsByUserId() {
        // Given
        long requestUserId = 1L;
        RequestUser requester = new RequestUser(requestUserId, Role.ADMIN);
        long userId = 2L;
        long postId = 1L;
        PostEntity userDraftPost = PostEntity.builder()
            .id(postId)
            .title("User Draft Post")
            .content("This is a draft post by the user.")
            .readStatus(PublicationStatus.DRAFTING)
            .userId(userId)
            .build();

        doNothing().when(this.userService).validUserById(requestUserId);
        doNothing().when(this.userService).validUserById(userId);
        when(this.postRepository.findAllByUserId(userId)).thenReturn(List.of(userDraftPost));

        // When
        List<PostSummaryDto> userPosts = this.postService.getAllPostsByUserId(requester, userId);

        // Then
        assertEquals(1, userPosts.size());
        assertTrue(userPosts.stream().anyMatch((p) -> p.postId().equals(userDraftPost.getId())));
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {
        // Given
        long userId = 1L;
        RequestUser requester = new RequestUser(userId, Role.USER);
        long postId = 1L;

        when(this.postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> this.postService.getPost(requester, postId));
    }

}
