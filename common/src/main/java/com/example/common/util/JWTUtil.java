package com.example.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class JWTUtil {

    public static boolean isContainsRole(JwtAuthenticationToken principal, String role){
        Collection<String> roles = principal.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());
        return roles.contains(role);
    }

    public static boolean isAuthorOrAdmin(JwtAuthenticationToken principal, String userId){
        return isContainsRole(principal, "ADMIN") && principal.getName().equals(userId);
    }

    public static boolean isAuthorOrModer(JwtAuthenticationToken principal, String userId){
        return isContainsRole(principal, "ADMIN") && principal.getName().equals(userId);
    }
}