package com.example.user.controller.kc;

import com.example.user.model.dto.UserDTO;
import com.example.user.servise.kc.KeycloakUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.user.util.UserUtil.addRoles;
import static com.example.user.util.UserUtil.checkModificationAllowed;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = KeycloakUserController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Profile("kc")
public class KeycloakUserController {

    public static final String REST_URL = "/api/v1/kc-users/";
    private final KeycloakUserService userService;

    @Operation(summary = "Get a user by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        log.info("get {}", id);
        UserDTO user = userService.get(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(summary = "Get a current user profile", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/profile")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal Jwt jwt) {
        log.info("get profile for {}", jwt.getSubject());
        UserDTO profile = userService.get(jwt.getSubject());
        addRoles(profile, jwt);
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @Operation(summary = "Return a list of users and filtered according the query parameters", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll(
            @RequestParam(defaultValue = "", required = false) String filter

    ) {
        log.info("getAll");
        List<UserDTO> users = userService.getAll(filter);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @Operation(summary = "Create a new user", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserDTO user) {
        log.info("create {}", user);
        List<String> roles = List.of("ADMIN", "USER");
        UserDTO savedUser = userService.saveUser(user, roles);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Operation(summary = "Update a user by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO user, @PathVariable String id) {
        log.info("update {} with id={}", user, id);
        checkModificationAllowed(id);
        assureIdConsistent(user, id);
        UserDTO updatedUser = userService.updateUser(user, id);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user account by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        log.info("delete {}", id);
        checkModificationAllowed(id);
        userService.deleteUser(id);
    }
}