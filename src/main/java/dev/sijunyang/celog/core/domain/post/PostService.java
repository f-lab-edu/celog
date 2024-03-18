package dev.sijunyang.celog.core.domain.post;

import java.util.List;
import java.util.stream.Collectors;

import dev.sijunyang.celog.core.global.enums.PublicationStatus;

import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostDto createPost(PostDto postDto) {
        PostEntity postEntity = mapToPostEntity(postDto);
        PostEntity savedPost = this.postRepository.save(postEntity);
        return mapToPostDto(savedPost);
    }

    public PostDto updatePost(Long postId, UpdatePostRequest updateRequest) {
        PostEntity oldPostEntity = getById(postId);

        PostEntity newPostEntity = PostEntity.builder()
            .id(oldPostEntity.getId())
            .title(updateRequest.title())
            .content(updateRequest.content())
            .readStatus(updateRequest.readStatus())
            .userId(oldPostEntity.getUserId())
            .build();

        PostEntity updatedPost = this.postRepository.save(newPostEntity);
        return mapToPostDto(updatedPost);
    }

    public void deletePost(Long postId) {
        PostEntity postEntity = getById(postId);
        this.postRepository.delete(postEntity);
    }

    public PostDto getPost(Long postId) {
        PostEntity postEntity = getById(postId);
        return mapToPostDto(postEntity);
    }

    public List<PostSummaryDto> getAllPublishedPosts() {
        return this.postRepository.findAll()
                .stream()
                .filter(this::isPublished)
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
    }

    public List<PostSummaryDto> getAllPostsByUserId(final Long userId) {
        return this.postRepository.findAll()
                .stream()
                .filter((post) -> isDraftingByUserId(post, userId) || isPublished(post))
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
    }

    private boolean isPublished(PostEntity post) {
        return post.getReadStatus().equals(PublicationStatus.PUBLIC_PUBLISHED);
    }

    private boolean isDraftingByUserId(PostEntity post, Long userId) {
        return post.getReadStatus().equals(PublicationStatus.DRAFTING) && post.getUserId().equals(userId);
    }


    private PostEntity mapToPostEntity(PostDto postDto) {
        return PostEntity.builder()
            .id(postDto.postId())
            .title(postDto.title())
            .content(postDto.content())
            .readStatus(postDto.readStatus())
            .userId(postDto.userId())
            .build();
    }

    private PostDto mapToPostDto(PostEntity postEntity) {
        return new PostDto(postEntity.getId(), postEntity.getTitle(), postEntity.getContent(),
                postEntity.getReadStatus(), postEntity.getUserId(), postEntity.getModifiedAt(),
                postEntity.getCreatedAt());
    }

    private PostSummaryDto mapToPostSummaryDto(PostEntity postEntity) {
        return new PostSummaryDto(postEntity.getId(), postEntity.getTitle(), postEntity.getUserId(),
                postEntity.getModifiedAt(), postEntity.getCreatedAt());
    }

    private PostEntity getById(Long postId) {
        return this.postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
    }

}
