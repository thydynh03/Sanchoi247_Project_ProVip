package com.example.SanChoi247.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.example.SanChoi247.model.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
    // Secret key for signing the JWT
    private final String SECRET_KEY = "sanchoi247SecureKeyForJWTSigningXyZ123456789abcdefghijklmnopqrstuv";
    public final long JWT_EXPIRATION = 604800000L; // 7 days

    // Create SecretKey from the secret key string
    private SecretKey createSecretKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8); // Convert the secret key string to byte array
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA512"); // Create SecretKey for HS512 algorithm
    }

    // Generate token for the user
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
    
        return Jwts.builder()
        
                .setSubject(user.getUsername()) // Set the subject to the username
                .claim("uid", user.getUid()) // Add UID to the token
                .claim("avatar", user.getAvatar()) // Add avatar to the token
                .claim("name", user.getName()) // Add name to the token
                .setIssuedAt(now) // Set issue date
                .setExpiration(expiryDate) // Set expiration date
                .signWith(createSecretKey(), SignatureAlgorithm.HS512) // Sign the token
                .compact(); // Compact to a JWT string

    }

    // Get UID from the token
    public int getUidFromToken(String token) {
        Claims claims = Jwts.parserBuilder() // Create a JwtParserBuilder
                .setSigningKey(createSecretKey()) // Set the signing key
                .build() // Build the JwtParser
                .parseClaimsJws(token) // Parse the JWT
                .getBody(); // Get the claims body

        return claims.get("uid", Integer.class); // Retrieve UID from claims
    }

    // Optional: Get the avatar from the token
    public String getAvatarFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(createSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("avatar", String.class); // Retrieve avatar from claims
    }

    // Optional: Get the name from the token
    public String getNameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(createSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("name", String.class); // Retrieve name from claims
    }

    // Optional: Get the secret key if needed elsewhere
    public SecretKey getSecretKey() {
        return createSecretKey();
    }

}
