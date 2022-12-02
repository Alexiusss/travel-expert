package com.example.clients.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Set;

@FeignClient(name = "user", url = "${clients.auth.url}")
public interface AuthClient {
    @GetMapping(path = "/api/v1/auth/validate")
    AuthCheckResponse isAuth (@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization);

    @PostMapping(path = "/api/v1/users/authorList")
    List<AuthorDTO> getAuthorList(@RequestBody Set<String> authors);
}