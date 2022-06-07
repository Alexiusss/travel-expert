package com.example.restaurant_advisor_react.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static String secret;

    private static long jwtExpirationMs;

    //https://mkyong.com/spring/spring-inject-a-value-into-static-variables/
    @Value("${jwt.secret}")
    private void setSecret(String jwtSecret) {
        secret = jwtSecret;
    }

    @Value("${jwt.ExpirationMs}")
    private void setExpiration(long expiration) {
        jwtExpirationMs = expiration;
    }

    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String userName = getUserEmailFromToken(token);
        return (userName.equals(userDetails.getUsername()) && isTokenExpired(token));
    }

    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public static String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private static String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public static String getUserEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
}
