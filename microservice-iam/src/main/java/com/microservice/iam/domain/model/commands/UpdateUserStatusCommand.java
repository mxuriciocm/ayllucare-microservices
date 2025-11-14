package com.microservice.iam.domain.model.commands;

import com.microservice.iam.domain.model.valueobjects.UserStatus;

/**
 * UpdateUserStatusCommand - Command for updating user status in AylluCare/B4U.
 * <p>
 *     Used by administrators to change a user's status (ACTIVE, INACTIVE, LOCKED).
 *     Triggers UserStatusChangedEvent for audit and security monitoring.
 * </p>
 *
 * @param userId the ID of the user whose status will be updated
 * @param newStatus the new status to assign
 * @param reason optional reason for the status change
 * @param adminUserId the ID of the administrator making the change
 */
public record UpdateUserStatusCommand(
    Long userId,
    UserStatus newStatus,
    String reason,
    Long adminUserId
) {}

