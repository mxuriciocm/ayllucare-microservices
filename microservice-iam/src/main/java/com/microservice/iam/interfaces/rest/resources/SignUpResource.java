package com.microservice.iam.interfaces.rest.resources;

import java.util.List;

/**
 * SignUpResource - DTO for user registration in AylluCare/B4U.
 * Follows the original pattern with Resource suffix.
 */
public record SignUpResource(
    String firstName,
    String lastName,
    String email,
    String password,
    List<String> roles,
    String phoneNumber,
    String preferredLanguage
) {}

