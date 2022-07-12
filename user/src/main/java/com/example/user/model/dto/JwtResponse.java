package com.example.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@AllArgsConstructor
public class JwtResponse {
    String userId;
    String email;
    String accessToken;
    Set<GrantedAuthority> authorities;
}