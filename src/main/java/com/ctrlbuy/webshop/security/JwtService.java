package com.ctrlbuy.webshop.security;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Ändrad från jwt.expiration till jwt.expirationMs
    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT Secret är null eller saknas i application.properties");
        }
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        System.out.println("✅ JWT Secret och Expiration injicerade korrekt: " + jwtSecret + " | " + jwtExpirationMs);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .claim("sub", username)
                .claim("iat", System.currentTimeMillis() / 1000)
                .claim("exp", (System.currentTimeMillis() + jwtExpirationMs) / 1000)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .claim("sub", username)
                .claim("iat", System.currentTimeMillis() / 1000)
                .claim("exp", (System.currentTimeMillis() + refreshTokenExpirationMs) / 1000)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Optional<String> extractUsername(String token) {
        try {
            JwtParser parserInstance = Jwts.parser()
                    .verifyWith(signingKey)
                    .build();

            Jws<Claims> claimsJws = parserInstance.parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();

            return Optional.ofNullable(claims.get("sub", String.class));
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    public boolean isTokenValid(String token, String username) {
        Optional<String> extractedUsername = extractUsername(token);
        return extractedUsername.isPresent() && extractedUsername.get().equals(username);
    }
}