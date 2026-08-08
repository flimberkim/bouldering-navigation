package com.boulderingnavigation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String issue(AuthUser authUser) {
        Date now = new Date();
        return Jwts.builder()
                .subject(authUser.id().toString())
                .claim("email", authUser.email())
                .claim("nickname", authUser.nickname())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Optional<AuthUser> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            AuthUser authUser = new AuthUser(
                    Long.valueOf(claims.getSubject()),
                    claims.get("email", String.class),
                    claims.get("nickname", String.class));
            return Optional.of(authUser);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
