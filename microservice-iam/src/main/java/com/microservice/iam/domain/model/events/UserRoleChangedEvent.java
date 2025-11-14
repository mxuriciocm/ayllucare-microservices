package com.microservice.iam.domain.model.events;

import java.time.Instant;
import java.util.Set;

public record UserRoleChangedEvent(
    Long userId,
    String email,
    Set<String> previousRoles,
    Set<String> newRoles,
    Long changedBy,
    Instant occurredAt
) {}

