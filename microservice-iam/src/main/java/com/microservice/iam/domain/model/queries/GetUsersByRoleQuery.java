package com.microservice.iam.domain.model.queries;

/**
 * GetUsersByRoleQuery - Query for AylluCare/B4U platform.
 * <p>
 *     Retrieves all users with a specific role.
 *     Useful for admin operations and analytics.
 * </p>
 *
 * @param roleName the role name to filter by (e.g., "ROLE_PATIENT", "ROLE_DOCTOR")
 */
public record GetUsersByRoleQuery(String roleName) {}

