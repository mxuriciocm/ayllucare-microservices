package com.microservice.triage.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT Token Validator.
 * Validates JWT tokens from IAM microservice.
 */
@Component
public class JwtTokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    @Value("${authorization.jwt.secret}")
    private String secret;

    /**
     * Validates a JWT token and creates an authentication object.
     *
     * @param token The JWT token
     * @return JwtAuthenticationToken if valid, null otherwise
     */
    public JwtAuthenticationToken validateToken(String token) {
        try {
            logger.debug("Validating JWT token...");
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            logger.debug("JWT token parsed successfully. Claims: {}", claims);

            // Extract userId from 'id' claim (not from subject which contains email)
            Long userId = claims.get("id", Long.class);

            if (userId == null) {
                logger.error("JWT token does not contain 'id' claim. Available claims: {}", claims.keySet());
                return null;
            }

            logger.debug("Extracted userId: {}", userId);

            List<GrantedAuthority> authorities = extractAuthorities(claims);
            logger.debug("Extracted authorities: {}", authorities);

            JwtAuthenticationToken authToken = new JwtAuthenticationToken(userId, token, authorities);
            logger.info("JWT token validated successfully for userId: {} with authorities: {}", userId, authorities);

            return authToken;

        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        try {
            Object rolesObj = claims.get("roles");

            if (rolesObj instanceof List) {
                List<String> roles = (List<String>) rolesObj;
                for (String role : roles) {
                    // Roles already come with ROLE_ prefix from IAM
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            } else if (rolesObj instanceof String) {
                // Roles already come with ROLE_ prefix from IAM
                authorities.add(new SimpleGrantedAuthority((String) rolesObj));
            }

        } catch (Exception e) {
            logger.warn("Error extracting roles from token", e);
        }

        return authorities;
    }
}

