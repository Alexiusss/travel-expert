package com.example.apigw.service;

import com.example.apigw.utils.CookieUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
public class TokenService {

    @Autowired
    private CookieUtils cookieUtils;

    private static final String KEYCLOAK_URI = "http://localhost:8180/realms/travel-expert-realm/protocol/openid-connect";

    public static final RestTemplate restTemplate = new RestTemplate();

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

    public ResponseEntity<String> requestNewTokens(String code) throws JsonProcessingException {
        MultiValueMap<String, String> mapForm = initMapForm();
        mapForm.add("grant_type", grantTypeCode);
        mapForm.add("code", code.replaceAll("^\"|\"$", ""));
        mapForm.add("redirect_uri", clientUrl);

        return requestTokens(mapForm);
    }

    public ResponseEntity<String> refreshAccessToken(String oldRefreshToken) throws JsonProcessingException {
        MultiValueMap<String, String> mapForm = initMapForm();
        mapForm.add("grant_type", grantTypeRefresh);
        mapForm.add("refresh_token", oldRefreshToken);

        return requestTokens(mapForm);
    }

    public ResponseEntity<String> logout(String idToken) {
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

        ResponseEntity<String> response = TokenService.restTemplate.getForEntity(
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

    private MultiValueMap<String, String> initMapForm() {
        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("client_id", clientId);
        mapForm.add("client_secret", clientSecret);
        return mapForm;
    }

    private ResponseEntity<String> requestTokens(MultiValueMap<String, String> mapForm) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(KEYCLOAK_URI + "/token", HttpMethod.POST, request, String.class);

        HttpHeaders responseHeaders = cookieUtils.createCookies(response);

        return ResponseEntity.ok().headers(responseHeaders).build();
    }

    public void refreshAccessTokenAndAddBearerHeader(ServerWebExchange exchange, HttpHeaders headers) throws JsonProcessingException {
        ResponseEntity<String> response = refreshAccessToken(exchange.getRequest().getCookies().get("RT").get(0).getValue());

        if (response.getStatusCode() == HttpStatus.OK) {
            HttpHeaders responseHeaders = response.getHeaders();
            List<String> setCookieHeaders = responseHeaders.get(HttpHeaders.SET_COOKIE);

            if (setCookieHeaders != null) {
                exchange.getResponse().getHeaders().addAll(HttpHeaders.SET_COOKIE, setCookieHeaders);
            }

            String accessToken = extractTokenValue(responseHeaders.getFirst(HttpHeaders.SET_COOKIE), "AT");
            headers.setBearerAuth(accessToken);
            exchange.mutate().request(r -> r.headers(httpHeaders -> httpHeaders.addAll(headers))).build();
        }
    }

    public void addBearerHeader(ServerWebExchange exchange, HttpHeaders headers) {
        String accessToken = exchange.getRequest().getCookies().get("AT").get(0).getValue();
        headers.setBearerAuth(accessToken);
        exchange.mutate().request(r -> r.headers(httpHeaders -> httpHeaders.addAll(headers))).build();
    }

    private String extractTokenValue(String setCookieHeader, String tokenName) {
        String tokenKey = tokenName + "=";
        int tokenStartIndex = setCookieHeader.indexOf(tokenKey);
        if (tokenStartIndex == -1) {
            return null;
        }

        int tokenEndIndex = setCookieHeader.indexOf(";", tokenStartIndex);
        if (tokenEndIndex == -1) {
            tokenEndIndex = setCookieHeader.length();
        }

        return setCookieHeader.substring(tokenStartIndex + tokenKey.length(), tokenEndIndex);
    }
}