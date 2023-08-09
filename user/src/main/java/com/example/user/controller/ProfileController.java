package com.example.user.controller;

import com.example.user.model.dto.UserDTO;
import com.example.user.servise.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.user.util.UserUtil.checkModificationAllowed;
import static com.example.user.util.UserUtil.getAuthUserIdFromPrincipal;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(path = ProfileController.REST_URL, produces = APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyAuthority('MODERATOR', 'USER')")
@Profile({ "!test_kc & !kc" })
public class ProfileController {
    static final String REST_URL = "/api/v1/profile";

    @Autowired
    UserService userService;

    @Operation(summary = "Get a user profile by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<UserDTO> get(@AuthenticationPrincipal Object principal) {
        String authUserId = getAuthUserIdFromPrincipal(principal);
        log.info("get profile {}", authUserId);
        return ResponseEntity.ok(userService.getProfile(principal));
    }

    @Operation(summary = "Update a user profile by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> update(@AuthenticationPrincipal Object principal, @Valid @RequestBody UserDTO user) {
        String authUserId = getAuthUserIdFromPrincipal(principal);
        log.info("update {} with id={}", user, authUserId);
        assureIdConsistent(user, authUserId);
        checkModificationAllowed(authUserId);
        UserDTO updatedUser = userService.updateProfile(user, authUserId);
        return ResponseEntity.ok(updatedUser);
    }
}