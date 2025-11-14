package com.microservice.iam.interfaces.rest.dto.response;

import java.util.Set;

/**
 * AuthenticationResponse - DTO for authentication responses in AylluCare/B4U.
 * <p>
 *     Returned after successful login or registration.
 *     Simplified version without refresh tokens for MVP.
 * </p>
 *
 * @param userId the user's unique identifier
 * @param email the user's email address
 * @param firstName the user's first name
 * @param lastName the user's last name
 * @param roles the user's roles
 * @param status the user's account status
 * @param accessToken the JWT access token
 * @param tokenType the token type (typically "Bearer")
 * @param expiresIn the token expiration time in seconds
 */
public record AuthenticationResponse(
    Long userId,
    String email,
    String firstName,
    String lastName,
    Set<String> roles,
    String status,
    String accessToken,
    String tokenType,
    Long expiresIn
) {
    public AuthenticationResponse(Long userId, String email, String firstName, String lastName,
                                 Set<String> roles, String status, String accessToken) {
        this(userId, email, firstName, lastName, roles, status, accessToken, "Bearer", 604800L); // 7 days
    }
}

