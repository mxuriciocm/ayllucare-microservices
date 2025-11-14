package com.microservice.iam.interfaces.rest.dto.request;

/**
 * UpdateUserStatusRequest - DTO for updating user status in AylluCare/B4U.
 *
 * @param status the new status ("ACTIVE", "INACTIVE", or "LOCKED")
 * @param reason optional reason for the status change
 */
public record UpdateUserStatusRequest(
    String status,
    String reason
) {}

