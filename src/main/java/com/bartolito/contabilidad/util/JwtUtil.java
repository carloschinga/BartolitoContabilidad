package com.bartolito.contabilidad.util;


import java.nio.charset.StandardCharsets;
import java.security.Key;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.new.secret}")
    private String newSecret;

    @Value("${jwt.old.secret}")
    private String oldSecret;

    private Key newKey() { return Keys.hmacShaKeyFor(newSecret.getBytes(StandardCharsets.UTF_8)); }
    private Key oldKey() { return Keys.hmacShaKeyFor(oldSecret.getBytes(StandardCharsets.UTF_8)); }

    // ============ VALIDACIÓN ============
    public boolean validateToken(String token) {
        return parse(token) != null;
    }

    public Claims parse(String token) {
        Claims claims = tryParseClaims(token, newKey());
        if (claims != null) return claims;
        return tryParseClaims(token, oldKey());
    }

    private Claims tryParseClaims(String token, Key key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // ============ EXTRACCIÓN NORMALIZADA ============
    public String extractUsername(String token) {
        Claims claims = parse(token);
        if (claims == null) return null;
        String usuario = claims.get("usuario", String.class);
        return usuario != null ? usuario : claims.getSubject();
    }


}
