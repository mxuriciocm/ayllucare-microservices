package com.microservice.iam.domain.model.queries;

import com.microservice.iam.domain.model.valueobjects.Roles;

/**
 * Query to get a role by name for AylluCare/B4U.
 */
public record GetRoleByNameQuery(Roles roleName) {
}
