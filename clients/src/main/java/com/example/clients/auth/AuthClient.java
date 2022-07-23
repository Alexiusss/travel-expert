package com.example.clients.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user", url = "${clients.auth.url}")
public interface AuthClient {
    @GetMapping(path = "/api/v1/auth/validate")
    AuthCheckResponse isAuth (@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization);
}