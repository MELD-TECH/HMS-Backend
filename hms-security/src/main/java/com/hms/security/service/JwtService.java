package com.hms.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${JWT_SECRET_KEY}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long expiration;

    public String generateToken(String username) {
        return generateToken(Map.of(), username);
    }

    public String generateToken(
            Map<String, Object> claims,
            String username) {

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    public boolean isTokenValid(
            String token,
            String username) {

        String extractedUsername =
                extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        ).before(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes()
        );
    }
   
    public LocalDateTime extractExpiration(
            String token) {

        Date expirationDate =
                extractClaim(
                        token,
                        Claims::getExpiration
                );

        return expirationDate.toInstant()
                .atZone(
                        ZoneId.systemDefault()
                )
                .toLocalDateTime();
    }

 // Calculate remaining seconds until expiry
    public long getRemainingSeconds(String token) {
        try {
            LocalDateTime expiration = extractExpiration(token); // Returns LocalDateTime
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiration);
            
            return Math.max(0, seconds);
        } catch (Exception e) {
            e.getMessage(); // Catches ExpiredJwtException or parsing failures
            return 0; 
        }
    }
}