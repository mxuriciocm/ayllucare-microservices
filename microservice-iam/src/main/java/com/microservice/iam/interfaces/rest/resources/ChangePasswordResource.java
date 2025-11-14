package com.microservice.iam.interfaces.rest.resources;

/**
 * Resource for changing user password.
 */
public record ChangePasswordResource(
    String currentPassword,
    String newPassword
) {}
