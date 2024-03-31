package dev.sijunyang.celog.core.domain.post;

import dev.sijunyang.celog.core.domain.reply.ReplyService;
import dev.sijunyang.celog.core.domain.user.UserDto;
import dev.sijunyang.celog.core.domain.user.UserService;
import dev.sijunyang.celog.core.global.enums.PublicationStatus;
import dev.sijunyang.celog.core.global.enums.Role;
import dev.sijunyang.celog.core.global.error.nextVer.InsufficientAuthorizationException;
import dev.sijunyang.celog.core.global.error.nextVer.NotFoundResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        CreatePostRequest createPostRequest = new CreatePostRequest("Test Post", "This is a test post.",
                PublicationStatus.DRAFTING);
        PostEntity createdPostEntity = PostEntity.builder()
            .title(createPostRequest.title())
            .content(createPostRequest.content())
            .readStatus(createPostRequest.readStatus())
            .userId(userId)
            .build();

        when(postRepository.save(any())).thenReturn(createdPostEntity);
        doNothing().when(userService).validUserById(userId);

        // When
        postService.createPost(userId, createPostRequest);

        // Then
        verify(postRepository, times(1)).save(postEntityCaptor.capture());
        PostEntity capturedEntity = postEntityCaptor.getValue();
        assertEquals(createPostRequest.title(), capturedEntity.getTitle());
        assertEquals(createPostRequest.content(), capturedEntity.getContent());
        assertEquals(createPostRequest.readStatus(), capturedEntity.getReadStatus());
        assertEquals(userId, capturedEntity.getUserId());
    }

    @Test
    void shouldUpdateExistingPost() {
        // Given
        long userId = 1L;
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
        PostEntity updatedPostEntity = PostEntity.builder()
            .id(postId)
            .title(updateRequest.title())
            .content(updateRequest.content())
            .readStatus(updateRequest.readStatus())
            .userId(userId)
            .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPostEntity));
        when(postRepository.save(any())).thenReturn(updatedPostEntity);

        // When
        postService.updatePost(userId, postId, updateRequest);

        // Then
        verify(postRepository, times(1)).save(postEntityCaptor.capture());
        PostEntity capturedEntity = postEntityCaptor.getValue();
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
        long postId = 1L;
        PostEntity existingPostEntity = PostEntity.builder().id(postId).userId(userId).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPostEntity));
        doNothing().when(userService).validUserIsSelfOrAdmin(requestUserId, userId);
        doNothing().when(replyService).deleteAllByPostId(postId);

        // When
        postService.deletePost(userId, postId);

        // Then
        verify(postRepository, times(1)).delete(existingPostEntity);
    }

    @Test
    void shouldReturnExistingPost() {
        // Given
        long userId = 1L;
        long postId = 1L;
        PostEntity existingPostEntity = PostEntity.builder()
            .id(postId)
            .title("Test Post")
            .content("This is a test post.")
            .readStatus(PublicationStatus.PUBLIC_PUBLISHED)
            .userId(userId)
            .build();
        UserDto user = UserDto.builder().id(userId).role(Role.USER).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPostEntity));
        doNothing().when(userService).validUserById(userId);
        when(userService.getUserById(userId)).thenReturn(user);

        // When
        PostDto retrievedPostDto = postService.getPost(userId, postId);

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
        long userId = 1L;
        long otherUserId = 2L;
        UserDto otherUser = UserDto.builder().id(otherUserId).role(Role.USER).build();
        PostEntity existingPostEntity = PostEntity.builder()
            .id(postId)
            .title("Test Post")
            .content("This is a test post.")
            .readStatus(PublicationStatus.DRAFTING)
            .userId(userId)
            .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPostEntity));
        when(userService.getUserById(otherUserId)).thenReturn(otherUser);

        // When & Then
        assertThrows(InsufficientAuthorizationException.class, () -> postService.getPost(otherUserId, postId));
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

        when(postRepository.findAll()).thenReturn(List.of(publishedPost1, draftPost, publishedPost2));

        // When
        List<PostSummaryDto> publishedPosts = postService.getAllPublishedPosts();

        // Then
        assertEquals(2, publishedPosts.size());
        assertTrue(publishedPosts.stream().anyMatch(p -> p.postId().equals(publishedPost1.getId())));
        assertTrue(publishedPosts.stream().anyMatch(p -> p.postId().equals(publishedPost2.getId())));
    }

    @Test
    void shouldReturnAllPostsByUserId() {
        // Given
        long requestUserId = 1L;
        long userId = 2L;
        PostEntity userDraftPost = PostEntity.builder()
            .id(1L)
            .title("User Draft Post")
            .content("This is a draft post by the user.")
            .readStatus(PublicationStatus.DRAFTING)
            .userId(userId)
            .build();

        when(postRepository.findAllByUserId(userId)).thenReturn(List.of(userDraftPost));
        doNothing().when(userService).validUserIsSelfOrAdmin(requestUserId, userId);

        // When
        List<PostSummaryDto> userPosts = postService.getAllPostsByUserId(requestUserId, userId);

        // Then
        assertEquals(1, userPosts.size());
        assertTrue(userPosts.stream().anyMatch(p -> p.postId().equals(userDraftPost.getId())));
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {
        // Given
        long userId = 1L;
        long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundResourceException.class, () -> postService.getPost(userId, postId));
    }

}
