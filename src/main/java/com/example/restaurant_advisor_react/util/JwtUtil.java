package com.example.restaurant_advisor_react.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

@UtilityClass
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.ExpirationMs}")
    private int jwtExpirationMs;

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

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public static String getUserEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
}
