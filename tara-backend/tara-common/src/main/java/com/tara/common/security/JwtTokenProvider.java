package com.tara.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtTokenProvider(
        @Value("${tara.jwt.secret:EnterprisePromptOpsSecretKeyMustBeAtLeast256BitsLongForHMACSHA512Security}") String secret,
        @Value("${tara.jwt.access-token-expiration-ms:86400000}") long accessTokenValidityMs,
        @Value("${tara.jwt.refresh-token-expiration-ms:604800000}") long refreshTokenValidityMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
    }

    public String generateAccessToken(UUID userId, String email, UUID orgId, UUID workspaceId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityMs);

        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("orgId", orgId != null ? orgId.toString() : null)
            .claim("workspaceId", workspaceId != null ? workspaceId.toString() : null)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityMs);

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public UUID getUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }
}
