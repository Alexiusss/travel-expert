package com.example.user.controller;

import com.example.clients.auth.AuthorDTO;
import com.example.user.annotation.AdminOrModerRoleAccess;
import com.example.user.annotation.AdminRoleAccess;
import com.example.user.annotation.UserRoleAccess;
import com.example.user.model.dto.UserDTO;
import com.example.user.servise.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.user.util.UserUtil.checkModificationAllowed;
import static com.example.user.util.UserUtil.getAuthUserIdFromPrincipal;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = UserController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

    public static final String REST_URL = "/api/v1/users";

    @Autowired
    UserService userService;

    @Operation(summary = "Get a user by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @AdminRoleAccess
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        log.info("get {}", id);
        final UserDTO user = userService.get(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get a author DTO by its id")
    @GetMapping(value = "/{id}/author")
    public ResponseEntity<AuthorDTO> getAuthor(@PathVariable String id) {
        log.info("get author for {}", id);
        final AuthorDTO author = userService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    @Operation(summary = "Get a list of author DTO by id array")
    @PostMapping("/authorList")
    public ResponseEntity<List<AuthorDTO>> getAuthorList(@RequestBody String[] authors) {
        log.info("get authorList for {}", Arrays.toString(authors));
        final List<AuthorDTO> authorList = userService.getAllAuthorsById(authors);
        return ResponseEntity.ok(authorList);
    }

    @Operation(summary = "Get a author DTO by username")
    @GetMapping(value = "/{username}/authorByUsername")
    public ResponseEntity<AuthorDTO> getByAuthorUsername(@PathVariable String username) {
        log.info("get authorName for {}", username);
        final AuthorDTO user = userService.getAuthorByUserName(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Return a list of users and filtered according the query parameters", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @AdminOrModerRoleAccess
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "", required = false) String filter
    ) {
        log.info("getAll");
        return userService.getAll(page, size, filter);
    }

    @Operation(summary = "Create a new user", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @AdminRoleAccess
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserDTO user) {
        log.info("create {}", user);
        UserDTO savedUser = userService.saveUser(user, user.getRoles());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Operation(summary = "Update a user by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @AdminRoleAccess
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO user, @PathVariable String id) {
        log.info("update {} with id={}", user, id);
        checkModificationAllowed(id);
        assureIdConsistent(user, id);
        UserDTO updatedUser = userService.updateUser(user, id);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Enable/disable a user account by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @AdminOrModerRoleAccess
    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enableUser(@PathVariable String id, @RequestParam boolean enable) {
        log.info(enable ? "enable {}" : "disable {}", id);
        checkModificationAllowed(id);
        userService.enableUser(id, enable);
    }

    @Operation(summary = "Delete a user account by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @AdminRoleAccess
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        log.info("delete {}", id);
        checkModificationAllowed(id);
        userService.deleteUser(id);
    }

    @Operation(summary = "Subscribe to user per its id")
    @UserRoleAccess
    @GetMapping("subscribe/{userId}")
    public void subscribe(@AuthenticationPrincipal Object principal, @PathVariable String userId) {
        String authUserId = getAuthUserIdFromPrincipal(principal);
        log.info("user {} subscribe to user {}", authUserId, userId);
        userService.subscribe(authUserId, userId);
    }

    @Operation(summary = "Unsubscribe from the user by his id")
    @UserRoleAccess
    @GetMapping("unSubscribe/{userId}")
    public void unSubscribe(@AuthenticationPrincipal Object principal, @PathVariable String userId) {
        String authUserId = getAuthUserIdFromPrincipal(principal);
        log.info("user {} unsubscribe from user {}", authUserId, userId);
        userService.unSubscribe(authUserId, userId);
    }
}