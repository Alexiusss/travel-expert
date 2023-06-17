package com.example.apigw.controller;

import com.example.apigw.service.BFFAuthService;
import com.example.apigw.utils.CookieUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = BFFAuthController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Profile("kc")
public class BFFAuthController {

    public static final String REST_URL = "/api/v1/bff";

    @Autowired
    private CookieUtils cookieUtils;

    @Autowired
    private BFFAuthService bffAuthService;

    private static final String KEYCLOAK_URI = "http://localhost:8180/realms/travel-expert-realm/protocol/openid-connect";

    @Value("${keycloak.granttype.code}")
    private String grantTypeCode;

    @Value("${keycloak.granttype.refresh}")
    private String grantTypeRefresh;

    @Value("${keycloak.clientid}")
    private String clientId;

    @Value("${keycloak.secret}")
    private String clientSecret;

    @Value("${client.url}")
    private String clientUrl;

    @PostMapping("/token")
    public ResponseEntity<String> token(@RequestBody(required = false) String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", grantTypeCode);
        mapForm.add("client_id", clientId);
        mapForm.add("client_secret", clientSecret);
        mapForm.add("code", code);
        mapForm.add("redirect_uri", clientUrl);

        return bffAuthService.requestTokens(headers, mapForm);
    }

    @GetMapping("/newaccesstoken")
    public ResponseEntity<String> newAccessToken(@CookieValue("RT") String oldRefreshToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", grantTypeRefresh);
        mapForm.add("client_id", clientId);
        mapForm.add("client_secret", clientSecret);
        mapForm.add("refresh_token", oldRefreshToken);

        return bffAuthService.requestTokens(headers, mapForm);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("IT") String idToken) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(KEYCLOAK_URI + "/logout")
                .queryParam("post_logout_redirect_uri", "{post_logout_redirect_uri}")
                .queryParam("id_token_hint", "{id_token_hint}")
                .queryParam("client_id", "{client_id}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("post_logout_redirect_uri", clientUrl);
        params.put("id_token_hint", idToken);
        params.put("client_id", clientId);

        ResponseEntity<String> response = BFFAuthService.restTemplate.getForEntity(
                urlTemplate,
                String.class,
                params
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            HttpHeaders responseHeaders = cookieUtils.clearCookies();

            return ResponseEntity.ok().headers(responseHeaders).build();
        }

        return ResponseEntity.badRequest().build();
    }
}