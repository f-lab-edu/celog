package dev.sijunyang.celog.api;

import java.util.List;

import dev.sijunyang.celog.core.domain.post.CreatePostRequest;
import dev.sijunyang.celog.core.domain.post.PostDto;
import dev.sijunyang.celog.core.domain.post.PostService;
import dev.sijunyang.celog.core.domain.post.PostSummaryDto;
import dev.sijunyang.celog.core.domain.post.UpdatePostRequest;
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
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final AuthenticatedUserManager authenticatedUserManager;

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody CreatePostRequest createPostRequest) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        this.postService.createPost(requestUser, createPostRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId,
            @RequestBody UpdatePostRequest updatePostRequest) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        this.postService.updatePost(requestUser, postId, updatePostRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        this.postService.deletePost(requestUser, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        return ResponseEntity.ok().body(this.postService.getPost(requestUser, postId));
    }

    @GetMapping("/published")
    public ResponseEntity<List<PostSummaryDto>> getAllPublishedPosts() {
        return ResponseEntity.ok().body(this.postService.getAllPublishedPosts());
    }

    // TODO @GetMapping("/v1/users/{userId}/posts")로 하고 싶은데, 그럼 User 컨트롤러로 가거나 새로운 컨트롤러를 만들어야 함
    //  User 컨트롤러로 가면 계속 User 쪽으로 의존성이 모여서 별로라고 생각함, 메서드 하나 때문에 새로 만들기 애매함
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PostSummaryDto>> getAllPostsByUserId(@PathVariable Long userId) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        return ResponseEntity.ok().body(this.postService.getAllPostsByUserId(requestUser, userId));
    }
}
