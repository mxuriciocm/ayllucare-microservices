package com.microservice.iam.domain.model.events;

import java.time.Instant;

public record UserRegisteredAsDoctorEvent(
    Long userId,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    Instant occurredAt
) {}

