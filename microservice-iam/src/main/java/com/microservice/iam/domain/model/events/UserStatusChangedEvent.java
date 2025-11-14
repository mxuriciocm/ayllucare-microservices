package com.microservice.iam.domain.model.events;

import java.time.Instant;

public record UserStatusChangedEvent(
    Long userId,
    String email,
    String previousStatus,
    String newStatus,
    String reason,
    Long changedBy,
    Instant occurredAt
) {}

