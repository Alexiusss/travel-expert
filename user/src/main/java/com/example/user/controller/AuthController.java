package com.example.user.controller;

import com.example.clients.auth.AuthCheckResponse;
import com.example.user.AuthUser;
import com.example.user.model.User;
import com.example.user.model.dto.AuthRequest;
import com.example.user.model.dto.JwtResponse;
import com.example.user.model.dto.RegistrationDTO;
import com.example.user.servise.AuthService;
import com.example.user.servise.UserService;
import com.example.user.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.example.user.controller.UserController.REST_URL;
import static com.example.user.util.JwtUtil.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(path = AuthController.AUTH_URL, produces = APPLICATION_JSON_VALUE)
@Profile("!kc")
public class AuthController {

    public static final String AUTH_URL = "/api/v1/auth";

    @Value("${cookies.domain}")
    private String domain;

    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;

    @Operation(summary = "Log in using email and password")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login {}", request.getEmail());
        AuthUser user = authService.getAuthUser(request);

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        ResponseCookie cookie = generateCookie(refreshToken, domain);

        JwtResponse jwtResponse = new JwtResponse(user.id(), user.getUser().getEmail(), accessToken, Set.copyOf(user.getAuthorities()));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(jwtResponse);
    }

    @Operation(summary = "Registration of a new user account")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDTO registration) {
        log.info("registration {}", registration);
        User created = authService.registerUser(registration);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(summary = "Activating a user account via email confirmation code")
    @GetMapping("/activate/{code}")
    public ResponseEntity<?> activate(@PathVariable String code) {
        boolean isActivated = authService.activateUser(code);

        if (isActivated) {
            return ResponseEntity.ok("User successfully activated");
        } else {
            return ResponseEntity.ok("Activation code not found!");
        }
    }

    @Operation(summary = "JWT token validation")
    @GetMapping("/validate")
    public ResponseEntity<AuthCheckResponse> validateToken(@RequestHeader(name = "Authorization", defaultValue = "No token") String authorization,
                                                           @AuthenticationPrincipal AuthUser user) {
        String accessToken = authorization.split(" ")[1].trim();
        if (JwtUtil.validateAccessToken(accessToken, user)) {
            log.info("validate token for {}", user.getUser().getId());
            return ResponseEntity.ok(new AuthCheckResponse(user.id(), user.getAuthorities()));
        } else {
            return ResponseEntity.ok(new AuthCheckResponse("", Collections.emptyList()));
        }
    }

    @Operation(summary = "Refresh access token using refresh token")
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh-token") String refreshToken) {
        UserDetails userDetails = userService.loadUserByUsername(getUserEmailFromRefreshToken(refreshToken));
        log.info("refresh token for {}", userDetails.getUsername());
        if (JwtUtil.validateRefreshToken(refreshToken, userDetails)) {
            String accessToken = generateAccessToken(userDetails);
            return ResponseEntity.ok().body(Map.of("accessToken", accessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Logoff from the system")
    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = generateLogoutCookie(domain);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).body("logout");
    }
}