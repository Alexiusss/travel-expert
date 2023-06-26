package com.example.apigw.controller;

import com.example.apigw.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = BFFAuthController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Profile("kc")
public class BFFAuthController {

    public static final String REST_URL = "/api/v1/bff";

    @Autowired
    private TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<String> token(@RequestBody(required = false) String code) throws JsonProcessingException {
        return tokenService.requestNewTokens(code);
    }

    @GetMapping("/newaccesstoken")
    public ResponseEntity<String> newAccessToken(@CookieValue("RT") String oldRefreshToken) throws JsonProcessingException {
        return tokenService.refreshAccessToken(oldRefreshToken);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("IT") String idToken) {
        return tokenService.logout(idToken);
    }
}