package com.microservice.iam.infrastructure.tokens.jwt;

import com.microservice.iam.application.internal.outboundservices.tokens.TokenService;
import org.springframework.security.core.Authentication;

public interface BearerTokenService extends TokenService {
    /**
     * Generate the bearer token.
     *
     * @param authentication the {@link Authentication} authentication
     * @return the bearer token
     */
    String generateToken(Authentication authentication);
}
