package com.microservice.anamnesis.infrastructure.security;

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
 * JWT token validator for validating tokens issued by IAM microservice.
 */
@Component
public class JwtTokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthenticationToken validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            Long userId = Long.parseLong(subject);

            List<GrantedAuthority> authorities = extractAuthorities(claims);

            return new JwtAuthenticationToken(userId, token, authorities);

        } catch (Exception e) {
            logger.error("Invalid JWT token", e);
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
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            } else if (rolesObj instanceof String) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesObj));
            }

        } catch (Exception e) {
            logger.warn("Error extracting roles from token", e);
        }

        return authorities;
    }
}

