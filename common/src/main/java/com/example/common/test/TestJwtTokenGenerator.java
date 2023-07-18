package com.example.common.test;

import lombok.experimental.UtilityClass;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class TestJwtTokenGenerator {

    public static String generateAccessToken(RsaJsonWebKey rsaJsonWebKey, List<String> roles) throws JoseException {
        JwtClaims claims = generateClaims(roles);
        JsonWebSignature jws = generateJws(claims, rsaJsonWebKey);
        return jws.getCompactSerialization();
    }

    public static RsaJsonWebKey generateJwk() throws JoseException {
        RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
        rsaJsonWebKey.setKeyId("k1");
        rsaJsonWebKey.setAlgorithm(AlgorithmIdentifiers.RSA_USING_SHA256);
        rsaJsonWebKey.setUse("sig");
        return rsaJsonWebKey;
    }

    private static JwtClaims generateClaims(List<String> roles) {
        JwtClaims claims = new JwtClaims();
        claims.setJwtId(UUID.randomUUID().toString());
        claims.setExpirationTimeMinutesInTheFuture(30);
        claims.setNotBeforeMinutesInThePast(0);
        claims.setIssuedAtToNow();
        claims.setAudience("account");
        claims.setIssuer("http://localhost:8180/realms/travel-expert-realm");
        claims.setSubject(UUID.randomUUID().toString());
        claims.setClaim("typ", "Bearer");
        claims.setClaim("azp", "travel-expert-client");
        claims.setClaim("auth_time", NumericDate.fromMilliseconds(Instant.now().minus(11, ChronoUnit.SECONDS).toEpochMilli()).getValue());
        claims.setClaim("session_state", UUID.randomUUID().toString());
        claims.setClaim("acr", "1");
        claims.setClaim("realm_access", Map.of("roles", roles));
        claims.setClaim("resource_access", Map.of("account",
                        Map.of("roles", List.of("manage-account", "manage-account-links", "view-profile"))
                )
        );
        claims.setClaim("scope", "openid address email profile");
        claims.setClaim("name", "Test Name");
        claims.setClaim("email_verified", true);
        claims.setClaim("preferred_username", "test.name");
        claims.setClaim("given_name", "Test");
        claims.setClaim("family_name", "Name");
        claims.setClaim("address", "");
        claims.setClaim("email", "test@gmail.com");
        claims.setClaim("locale", "en");
        return claims;
    }

    private static JsonWebSignature generateJws(JwtClaims claims, RsaJsonWebKey rsaJsonWebKey) {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        jws.setHeader("typ", "JWT");
        return jws;
    }
}