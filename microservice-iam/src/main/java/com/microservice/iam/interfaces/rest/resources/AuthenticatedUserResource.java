package com.microservice.iam.interfaces.rest.resources;

/**
 * AuthenticatedUserResource - DTO for authentication response in AylluCare/B4U.
 * Follows the original pattern with Resource suffix.
 */
public record AuthenticatedUserResource(
    Long id,
    String email,
    String token
) {}

