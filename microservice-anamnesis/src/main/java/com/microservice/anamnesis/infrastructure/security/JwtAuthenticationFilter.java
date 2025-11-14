package com.microservice.anamnesis.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenValidator jwtTokenValidator;

    public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            String token = extractTokenFromRequest(request);

            if (token != null) {
                logger.debug("Token extracted from request: {}...", token.substring(0, Math.min(token.length(), 20)));
                JwtAuthenticationToken authentication = jwtTokenValidator.validateToken(token);

                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("✅ Authenticated user with ID: {} and authorities: {}",
                            authentication.getUserId(), authentication.getAuthorities());
                } else {
                    logger.warn("⚠️ Token validation returned null");
                }
            } else {
                logger.warn("⚠️ No JWT token found in Authorization header");
            }

        } catch (Exception e) {
            logger.error("❌ Error processing JWT authentication", e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}

