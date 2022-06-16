package com.example.restaurant_advisor_react.controller;

import ch.qos.logback.core.util.Duration;
import com.example.restaurant_advisor_react.AuthUser;
import com.example.restaurant_advisor_react.model.dto.AuthRequest;
import com.example.restaurant_advisor_react.model.dto.JwtResponse;
import com.example.restaurant_advisor_react.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
@RestController
@RequestMapping(path = AuthController.AUTH_URL, produces = APPLICATION_JSON_VALUE)
public class AuthController {

    static final String AUTH_URL = "/api/v1/auth";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${cookies.domain}")
    private String domain;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(), request.getPassword()
                            )
                    );

            AuthUser user = (AuthUser) authentication.getPrincipal();

            String accessToken = JwtUtil.generateToken(user);

            ResponseCookie cookie = ResponseCookie.from("access-token", accessToken)
                    .domain(domain)
                    .path("/")
                    .maxAge(Duration.buildByDays(365).getMilliseconds())
                    .secure(true)
                    .build();

            JwtResponse jwtResponse = new JwtResponse(user.id(), user.getUser().getEmail(),accessToken, "");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(jwtResponse);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@CookieValue(name = "access-token") String token, @AuthenticationPrincipal AuthUser user) {
        try {
            Boolean isValidToken = JwtUtil.validateToken(token, user);
            return ResponseEntity.ok(isValidToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout () {
        ResponseCookie cookie = ResponseCookie.from("access-token", "")
                .domain(domain)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).body("logout");
    }
}