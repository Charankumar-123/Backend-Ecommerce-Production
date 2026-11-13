package com.eoxys.service;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    // Stable 512-bit valid Base64-encoded secret key for HMAC-SHA JWT signing
    private static final String DEFAULT_SECRET = "TXlFQ29tbWVyY2VTZWNyZXRLZXlGb3JKd3RTaWduaW5nTXVzdEJlQXRMZWFzdDI1NkJpdHNMb25nMTIzNDU2Nzg=";
    private String secretKey = DEFAULT_SECRET;

    public JWTService() {
        this.secretKey = DEFAULT_SECRET;
    }

    // Generate token using email and role
    public String generateToken(String email) {
        return generateToken(email, "CUSTOMER");
    }

    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email) // Use email as the subject
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours  
                .and()
                .signWith(getKey())
                .compact();
    }

    // Extract email from the token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject); // Subject is now email
    }

    // Extract role from the token
    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Validate token by comparing extracted email with UserDetails
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token); // Extract email instead of username
        System.out.println("Extracted email: " + email);
        System.out.println("UserDetails username: " + userDetails.getUsername());
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token)); // userDetails.getUsername() returns email
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis())); // Fixed to compare with current time
    }

    private java.util.Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}