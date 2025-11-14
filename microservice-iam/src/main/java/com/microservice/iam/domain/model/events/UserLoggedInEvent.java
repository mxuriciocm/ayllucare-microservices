package com.microservice.iam.domain.model.events;

import java.time.Instant;
import java.util.Set;

public record UserLoggedInEvent(
    Long userId,
    String email,
    Set<String> roles,
    String ipAddress,
    String userAgent,
    Instant occurredAt
) {}

