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


    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(refreshTokenSecret).parseClaimsJws(token).getBody();
        //return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public static Boolean validateToken(String token, UserDetails userDetails) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        final String username = getUserEmailFromToken(token);
        return (userDetails != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
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
                //.path("/api/v1/auth")
                .path("/")
                .maxAge(Duration.buildByDays(30).getMilliseconds())
                .secure(true)
                .build();
    }
    public static String getUserEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
}
