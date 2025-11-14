package com.microservice.iam.domain.model.events;

import java.time.Instant;
import java.util.Set;

/**
 * UserCreatedEvent - Domain event for AylluCare/B4U platform.
 * <p>
 *     Published when a new user is registered in the system.
 *     Other bounded contexts (Profile & Consent, CaseDesk, etc.) can subscribe to this event
 *     to initialize user-specific data in their contexts.
 * </p>
 *
 * @param userId The unique identifier of the newly created user
 * @param email The email address of the user
 * @param firstName The user's first name
 * @param lastName The user's last name
 * @param roles The set of role names assigned to the user (e.g., "ROLE_PATIENT")
 * @param status The initial status of the user account
 * @param occurredAt The timestamp when the event occurred
 */
public record UserCreatedEvent(
    Long userId,
    String email,
    String firstName,
    String lastName,
    Set<String> roles,
    String status,
    Instant occurredAt
) {}
