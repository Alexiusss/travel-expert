package com.example.restaurant_advisor_react.util;

import ch.qos.logback.core.util.Duration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.time.LocalDateTime.now;

@Component
public class JwtUtil {

    private static String accessTokenSecret;
    private static String refreshTokenSecret;
    private static long accessTokenExpiration;
    private static long refreshTokenExpiration;

    //https://mkyong.com/spring/spring-inject-a-value-into-static-variables/
    @Value("{access.token.secret}")
    private void setAccessTokenSecret(String secret) {
        accessTokenSecret = secret;
    }

    @Value("{refresh.token.secret}")
    private void setRefreshTokenSecret(String secret) {
        refreshTokenSecret = secret;
    }

    @Value("${access.token.expiration}")
    private void setAccessTokenExpiration(long expiration) {
        accessTokenExpiration = expiration;
    }

    @Value("${refresh.token.expiration}")
    private void setRefreshTokenExpiration(long expiration) {
        refreshTokenExpiration = expiration;
    }


    public static Boolean validateAccessToken(String token, UserDetails userDetails) {
        return validateToken(token, userDetails, accessTokenSecret);
    }

    public static Boolean validateRefreshToken(String token, UserDetails userDetails) {
        return validateToken(token, userDetails, refreshTokenSecret);
    }

    public static Boolean validateToken(String token, UserDetails userDetails, String secret) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        final String username = getUserEmailFromToken(token, secret);
        return (userDetails != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token, secret));
    }

    public static String getUserEmailFromRefreshToken(String token) {
        return getClaimFromToken(token, Claims::getSubject, refreshTokenSecret);
    }

    public static String getUserEmailFromAccessToken(String token) {
        return getClaimFromToken(token, Claims::getSubject, accessTokenSecret);
    }

    public static String getUserEmailFromToken(String token, String secret) {
        return getClaimFromToken(token, Claims::getSubject, secret);
    }

    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, String secret) {
        final Claims claims = getAllClaimsFromToken(token, secret);
        return claimsResolver.apply(claims);
    }

    public static Claims getAllClaimsFromToken(String token, String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private static Boolean isTokenExpired(String token, String secret) {
        final Date expiration = getExpirationDateFromToken(token, secret);
        return expiration.before(new Date());
    }

    public static Date getExpirationDateFromToken(String token, String secret) {
        return getClaimFromToken(token, Claims::getExpiration, secret);
    }

    public static String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Instant accessExpiration = now().plusMinutes(accessTokenExpiration).atZone(ZoneId.systemDefault()).toInstant();
        return doGenerateToken(claims, userDetails.getUsername(), accessExpiration, accessTokenSecret);
    }

    public static String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        final Instant refreshExpiration = now().plusDays(refreshTokenExpiration).atZone(ZoneId.systemDefault()).toInstant();
        return doGenerateToken(claims, userDetails.getUsername(), refreshExpiration, refreshTokenSecret);
    }

    private static String doGenerateToken(Map<String, Object> claims, String subject, Instant expiration, String secret) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public static ResponseCookie generateCookie(String refreshToken, String domain) {
        return ResponseCookie.from("refresh-token", refreshToken)
                .domain(domain)
                .httpOnly(true)
                .path("/api/v1/auth")
                .maxAge(Duration.buildByDays(30).getMilliseconds() / 1000)
                .secure(true)
                .build();
    }

    public static ResponseCookie generateLogoutCookie(String domain) {
        return ResponseCookie.from("refresh-token", "")
                .domain(domain)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }
}
