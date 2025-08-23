package com.project.ecom.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtils {
    @Value("${spring.app.jwtSecret}")
    private String secret;

    // Getting JWT from header:
    public String getJWTTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken!=null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Generating a JWT token from username:
    public String generateJWTTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime()) + 300000))
                .signWith(getSecretKey())
                .compact();
    }

    // Getting username from JWT:
    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) getSecretKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }


    // Validating a JWT Token:
    public boolean validateJWTToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) getSecretKey())
                    .build()
                    .parseSignedClaims(authToken);

            // if above line executes without exception than return true
            return true;
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT Token: "+e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token expired: "+e.getMessage());
        }  catch (UnsupportedJwtException e) {
            System.out.println("JWT Token is unsupported: "+e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: "+e.getMessage());
        }
        return false;
    }


    // To create a sceret key:
    public Key getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
