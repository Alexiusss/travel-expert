package com.example.restaurant_advisor_react.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse {
    String userId;
    String email;
    String accessToken;
    String refreshToken;
}