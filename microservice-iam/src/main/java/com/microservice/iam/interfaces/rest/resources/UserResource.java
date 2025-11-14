package com.microservice.iam.interfaces.rest.resources;

import java.util.List;

/**
 * UserResource - DTO for user information in AylluCare/B4U.
 * Follows the original pattern with Resource suffix.
 */
public record UserResource(
    Long id,
    String email,
    String firstName,
    String lastName,
    List<String> roles,
    String status,
    String phoneNumber,
    String preferredLanguage
) {}

