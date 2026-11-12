package com.shop.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class JwtUtil {
    private final SecretKey key; private final long exp;
    public JwtUtil(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long exp) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes()); this.exp = exp;
    }
    public String generate(String email, String role) {
        return Jwts.builder().subject(email).claim("role", role).issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + exp)).signWith(key).compact();
    }
    public String getEmail(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject(); }
    public boolean valid(String token) { try { getEmail(token); return true; } catch (JwtException | IllegalArgumentException e) { return false; } }
}
