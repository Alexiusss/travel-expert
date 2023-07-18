package com.example.user.controller;

import com.example.clients.auth.AuthorDTO;
import com.example.user.AuthUser;
import com.example.user.model.User;
import com.example.user.servise.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = UserController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Slf4j
//@Profile("!kc")
public class UserController {
    static final String REST_URL = "/api/v1/users";
    @Autowired
    UserService userService;

    @Profile("!kc")
    @Operation(summary = "Get a user by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        log.info("get {}", id);
        final User user = userService.get(id);
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

    @Profile("!kc")
    @Operation(summary = "Return a list of users and filtered according the query parameters", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @GetMapping
    public Page<User> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("getAll");
        return userService.findAllPaginated(PageRequest.of(page, size));
    }

    @Profile("!kc")
    @Operation(summary = "Create a new user", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("create {}", user);
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Profile("!kc")
    @Operation(summary = "Update a user by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user, @PathVariable String id) {
        log.info("update {} with id={}", user, id);
        checkModificationAllowed(id);
        assureIdConsistent(user, id);
        User updatedUser = userService.updateUser(user, id);
        return ResponseEntity.ok(updatedUser);
    }

    @Profile("!kc")
    @Operation(summary = "Enable/disable a user account by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enableUser(@PathVariable String id, @RequestParam boolean enable) {
        log.info(enable ? "enable {}" : "disable {}", id);
        checkModificationAllowed(id);
        userService.enableUser(id, enable);
    }

    @Profile("!kc")
    @Operation(summary = "Delete a user account by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        log.info("delete {}", id);
        checkModificationAllowed(id);
        userService.deleteUser(id);
    }

    @Operation(summary = "Subscribe to user per its id")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("subscribe/{userId}")
    public void subscribe(@AuthenticationPrincipal AuthUser authUser, @PathVariable String userId){
        log.info("user {} subscribe to user {}", authUser.id(), userId);
        userService.subscribe(authUser, userId);
    }

    @Operation(summary = "Unsubscribe from the user by his id")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("unSubscribe/{userId}")
    public void unSubscribe(@AuthenticationPrincipal AuthUser authUser, @PathVariable String userId){
        log.info("user {} unsubscribe from user {}", authUser.id(), userId);
        userService.unSubscribe(authUser, userId);
    }
}