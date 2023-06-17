package com.example.apigw.service;

import com.example.apigw.utils.CookieUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class BFFAuthService {

    private final CookieUtils cookieUtils;

    private static final String KEYCLOAK_URI = "http://localhost:8180/realms/travel-expert-realm/protocol/openid-connect";

    public static final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> requestTokens(HttpHeaders headers, MultiValueMap<String, String> mapForm) throws JsonProcessingException {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(KEYCLOAK_URI + "/token", HttpMethod.POST, request, String.class);

        HttpHeaders responseHeaders = cookieUtils.createCookies(response);

        return ResponseEntity.ok().headers(responseHeaders).build();
    }
}