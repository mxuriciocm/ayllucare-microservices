package com.microservice.iam.domain.model.events;

import java.time.Instant;

public record UserRegisteredAsPatientEvent(
    Long userId,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String preferredLanguage,
    Instant occurredAt
) {}

