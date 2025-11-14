package com.microservice.iam.domain.model.valueobjects;

/**
 * UserStatus enum for AylluCare/B4U platform.
 * <p>
 *     Represents the status of a user account in the system:
 *     - ACTIVE: User can access the system normally
 *     - INACTIVE: User account is temporarily disabled
 *     - LOCKED: User account is locked (e.g., due to security reasons)
 * </p>
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    LOCKED
}

