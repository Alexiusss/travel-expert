package com.example.apigw.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Profile("kc")
public class CookieUtils {

    @Value("${cookie.domain}")
    private String cookieDomain;

    private static final String ID_TOKEN_COOKIE_KEY = "IT";

    private static final String ACCESS_TOKEN_COOKIE_KEY = "AT";

    private static final String REFRESH_TOKEN_COOKIE_KEY = "RT";

    public HttpHeaders createCookies(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response.getBody());

        String accessToken = root.get("access_token").asText();
        String idToken = root.get("id_token").asText();
        String refreshToken = root.get("refresh_token").asText();

        int accessTokenDuration = root.get("expires_in").asInt();
        int refreshTokenDuration = root.get("refresh_expires_in").asInt();

        HttpCookie accessTokenCookie = createCookie(ACCESS_TOKEN_COOKIE_KEY, accessToken, accessTokenDuration);
        HttpCookie refreshTokenCookie = createCookie(REFRESH_TOKEN_COOKIE_KEY, refreshToken, refreshTokenDuration);
        HttpCookie idTokenDurationCookie = createCookie(ID_TOKEN_COOKIE_KEY, idToken, accessTokenDuration);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, idTokenDurationCookie.toString());

        return responseHeaders;
    }

    private HttpCookie createCookie(String name, String value, int durationInSeconds) {
        return ResponseCookie
                .from(name, value)
                .maxAge(durationInSeconds)
                .sameSite("Strict")
                .httpOnly(true)
                .secure(true)
                .domain(cookieDomain)
                .path("/")
                .build();
    }

    public HttpHeaders clearCookies() {
        HttpCookie accessTokenCookie = deleteCookie(ACCESS_TOKEN_COOKIE_KEY);
        HttpCookie refreshTokenCookie = deleteCookie(REFRESH_TOKEN_COOKIE_KEY);
        HttpCookie idTokenCookie = deleteCookie(ID_TOKEN_COOKIE_KEY);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, idTokenCookie.toString());

        return responseHeaders;
    }

    private HttpCookie deleteCookie(String name) {
        return ResponseCookie
                .from(name, "")
                .maxAge(0)
                .sameSite("Strict")
                .httpOnly(true)
                .secure(true)
                .domain(cookieDomain)
                .path("/")
                .build();
    }
}