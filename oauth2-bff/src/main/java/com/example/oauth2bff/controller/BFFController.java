package com.example.oauth2bff.controller;

import com.example.oauth2bff.utils.CookieUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = BFFController.REST_URL, produces = APPLICATION_JSON_VALUE)
public class BFFController {

    public static final String REST_URL = "/bff";

    @Autowired
    private CookieUtils cookieUtils;

    private static final String KEYCLOAK_URI = "http://localhost:8180/realms/travel-expert-realm/protocol/openid-connect";

    private static final String ID_TOKEN_COOKIE_KEY = "IT";

    private static final String ACCESS_TOKEN_COOKIE_KEY = "AT";

    private static final String REFRESH_TOKEN_COOKIE_KEY = "RT";

    private static final RestTemplate restTemplate = new RestTemplate();

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

        return requestTokens(headers, mapForm);
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

        return requestTokens(headers, mapForm);
    }

    private HttpHeaders createCookies(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response.getBody());

        String accessToken = root.get("access_token").asText();
        String idToken = root.get("id_token").asText();
        String refreshToken = root.get("refresh_token").asText();

        int accessTokenDuration = root.get("expires_in").asInt();
        int refreshTokenDuration = root.get("refresh_expires_in").asInt();

        HttpCookie accessTokenCookie = cookieUtils.createCookie(ACCESS_TOKEN_COOKIE_KEY, accessToken, accessTokenDuration);
        HttpCookie refreshTokenCookie = cookieUtils.createCookie(REFRESH_TOKEN_COOKIE_KEY, refreshToken, refreshTokenDuration);
        HttpCookie idTokenDurationCookie = cookieUtils.createCookie(ID_TOKEN_COOKIE_KEY, idToken, accessTokenDuration);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, idTokenDurationCookie.toString());

        return responseHeaders;
    }

    private ResponseEntity<String> requestTokens(HttpHeaders headers, MultiValueMap<String, String> mapForm) throws JsonProcessingException {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(KEYCLOAK_URI + "/token", HttpMethod.POST, request, String.class);

        HttpHeaders responseHeaders = createCookies(response);

        return ResponseEntity.ok().headers(responseHeaders).build();
    }

    @GetMapping("/data")
    public ResponseEntity<String> data(@CookieValue("AT") String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8083/api/v1/kc-users/", HttpMethod.GET, request, String.class);

        return response;
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

        ResponseEntity<String> response = restTemplate.getForEntity(
                urlTemplate,
                String.class,
                params
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            HttpHeaders responseHeaders = clearCookies();

            return ResponseEntity.ok().headers(responseHeaders).build();
        }

        return ResponseEntity.badRequest().build();
    }

    private HttpHeaders clearCookies() {
        HttpCookie accessTokenCookie = cookieUtils.deleteCookie(ACCESS_TOKEN_COOKIE_KEY);
        HttpCookie refreshTokenCookie = cookieUtils.deleteCookie(REFRESH_TOKEN_COOKIE_KEY);
        HttpCookie idTokenCookie = cookieUtils.deleteCookie(ID_TOKEN_COOKIE_KEY);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, idTokenCookie.toString());

        return responseHeaders;
    }
}