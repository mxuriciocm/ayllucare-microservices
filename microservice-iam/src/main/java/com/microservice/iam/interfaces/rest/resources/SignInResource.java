package com.microservice.iam.interfaces.rest.resources;

/**
 * SignInResource - DTO for user login in AylluCare/B4U.
 * Follows the original pattern with Resource suffix.
 */
public record SignInResource(
    String email,
    String password
) {}

