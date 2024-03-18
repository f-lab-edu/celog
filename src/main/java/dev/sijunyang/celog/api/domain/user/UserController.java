package dev.sijunyang.celog.api.domain.user;

import dev.sijunyang.celog.api.AuthenticatedUserManager;
import dev.sijunyang.celog.core.domain.user.CreateUserRequest;
import dev.sijunyang.celog.core.domain.user.UpdateUserRequest;
import dev.sijunyang.celog.core.domain.user.UserDto;
import dev.sijunyang.celog.core.domain.user.UserService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/users")
public class UserController {

    private final AuthenticatedUserManager authenticatedUserManager;

    private final UserService userService;

    public UserController(AuthenticatedUserManager authenticatedUserManager, UserService userService) {
        this.authenticatedUserManager = authenticatedUserManager;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser() {
        Long userId = this.authenticatedUserManager.getId();
        return ResponseEntity.ok(this.userService.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(this.userService.createUser(request));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = this.authenticatedUserManager.getId();
        return ResponseEntity.ok(this.userService.updateUser(userId, request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        Long userId = this.authenticatedUserManager.getId();
        this.userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
