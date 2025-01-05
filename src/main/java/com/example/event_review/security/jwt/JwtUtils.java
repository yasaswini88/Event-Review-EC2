package com.example.event_review.security.jwt;
import com.example.event_review.Entity.User; 

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for generating and validating JWT tokens.
 */
@Component
public class JwtUtils {

    // We'll define these in application.properties
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    
    @Value("${spring.app.jwtExpirationMs}")
    private long jwtExpirationMs;
    
    /**
     * Generate a token from the user’s email (or username).
     */
    // public String generateJwtToken(String email) {
    //     Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    //     return Jwts.builder()
    //             .setSubject(email)
    //             .setIssuedAt(new Date())
    //             .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
    //             .signWith(key, SignatureAlgorithm.HS256)
    //             .compact();
    // }
    public String generateJwtToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
                .setSubject(user.getEmail()) // or user.getEmail()
                .claim("userId", user.getUserId())
                .claim("roleId", user.getRoles().getRoleId())  // e.g. 1,2,3,4
                // .claim("roleName", user.getRoles().getRoleName()) // e.g. "ROLE_USER", "ROLE_ADMIN"
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    

    /**
     * Extract email (subject) from the token.
     */
    public String getUserNameFromJwtToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject(); // The email is stored as the 'subject'
    }

    /**
     * Validate the token’s signature & expiration.
     */
    public boolean validateJwtToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
