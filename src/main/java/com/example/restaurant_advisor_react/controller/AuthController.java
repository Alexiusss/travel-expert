package com.example.restaurant_advisor_react.controller;

import com.example.restaurant_advisor_react.AuthUser;
import com.example.restaurant_advisor_react.model.User;
import com.example.restaurant_advisor_react.model.dto.AuthRequest;
import com.example.restaurant_advisor_react.model.dto.JwtResponse;
import com.example.restaurant_advisor_react.servise.AuthService;
import com.example.restaurant_advisor_react.servise.UserService;
import com.example.restaurant_advisor_react.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.example.restaurant_advisor_react.controller.UserController.REST_URL;
import static com.example.restaurant_advisor_react.util.JwtUtil.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        log.info("registration {}", user);
        User created = authService.registerUser(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @GetMapping("/activate/{code}")
    public ResponseEntity<?> activate(@PathVariable String code) {
        boolean isActivated = authService.activateUser(code);

        if (isActivated) {
            return ResponseEntity.ok("User successfully activated");
        } else {
            return ResponseEntity.ok("Activation code not found!");
        }
    }


    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh-token") String token) {
        UserDetails userDetails = userService.loadUserByUsername(getUserEmailFromRefreshToken(token));
        log.info("refresh token for {}", userDetails.getUsername());
        if (JwtUtil.validateRefreshToken(token, userDetails)) {
            String accessToken = generateAccessToken(userDetails);
            return ResponseEntity.ok().body(Map.of("accessToken", accessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = generateLogoutCookie(domain);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).body("logout");
    }
}