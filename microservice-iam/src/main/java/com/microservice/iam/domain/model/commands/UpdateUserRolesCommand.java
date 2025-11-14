package com.microservice.iam.domain.model.commands;

import com.microservice.iam.domain.model.valueobjects.Roles;

import java.util.Set;

/**
 * UpdateUserRolesCommand - Command for updating user roles in AylluCare/B4U.
 * <p>
 *     Used by administrators to change a user's roles.
 *     Triggers UserRoleChangedEvent for audit and downstream systems.
 * </p>
 *
 * @param userId the ID of the user whose roles will be updated
 * @param newRoles the new set of roles to assign
 * @param adminUserId the ID of the administrator making the change
 */
public record UpdateUserRolesCommand(
    Long userId,
    Set<Roles> newRoles,
    Long adminUserId
) {}

