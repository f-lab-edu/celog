package dev.sijunyang.celog.api;

import dev.sijunyang.celog.core.domain.user.CreateUserRequest;
import dev.sijunyang.celog.core.domain.user.RequestUser;
import dev.sijunyang.celog.core.domain.user.UpdateUserRequest;
import dev.sijunyang.celog.core.domain.user.UserDto;
import dev.sijunyang.celog.core.domain.user.UserService;
import jakarta.validation.Valid;

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
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticatedUserManager authenticatedUserManager;

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(this.userService.getUserById(userId));
    }

    // 실제론 사용되지 않는 인증 방식이지만, 성능테스트를 위해 많은 사용자를 만들어주어야 해서 사용한다.
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        this.userService.createUser(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        this.userService.updateUser(requestUser, userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        RequestUser requestUser = this.authenticatedUserManager.getRequestUser();
        this.userService.deleteUser(requestUser, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
