package com.example.restaurant_advisor_react.controller;

import com.example.restaurant_advisor_react.AuthUser;
import com.example.restaurant_advisor_react.model.dto.AuthRequest;
import com.example.restaurant_advisor_react.model.dto.JwtResponse;
import com.example.restaurant_advisor_react.servise.AuthService;
import com.example.restaurant_advisor_react.servise.UserService;
import com.example.restaurant_advisor_react.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Set;

import static com.example.restaurant_advisor_react.util.JwtUtil.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
@RestController
@RequestMapping(path = AuthController.AUTH_URL, produces = APPLICATION_JSON_VALUE)
public class AuthController {

    static final String AUTH_URL = "/api/v1/auth";

    @Value("${cookies.domain}")
    private String domain;

    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthUser user = authService.getAuthUser(request);

            String accessToken = generateAccessToken(user);
            String refreshToken = generateRefreshToken(user);

            ResponseCookie cookie = generateCookie(refreshToken, domain);

            JwtResponse jwtResponse = new JwtResponse(user.id(), user.getUser().getEmail(), accessToken, Set.copyOf(user.getAuthorities()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(jwtResponse);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh-token") String token, @AuthenticationPrincipal AuthUser user) {
        UserDetails userDetails = userService.loadUserByUsername(getUserEmailFromRefreshToken(token));
        if (JwtUtil.validateRefreshToken(token, userDetails)) {
            String accessToken = generateAccessToken(userDetails);
            return ResponseEntity.ok().body(Map.of("accessToken", accessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@CookieValue(name = "refresh-token") String token, @AuthenticationPrincipal AuthUser user) {
        try {
            Boolean isValidToken = JwtUtil.validateRefreshToken(token, user);
            return ResponseEntity.ok(isValidToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout () {
        ResponseCookie cookie = generateLogoutCookie(domain);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).body("logout");
    }
}