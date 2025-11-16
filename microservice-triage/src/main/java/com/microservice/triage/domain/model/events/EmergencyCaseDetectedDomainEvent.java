package com.microservice.triage.domain.model.events;

import java.time.Instant;
import java.util.List;

/**
 * Domain Event: Emergency Case Detected.
 * Published when a triage result is classified as EMERGENCY.
 * This is a critical domain event that requires immediate attention.
 */
public record EmergencyCaseDetectedDomainEvent(
    Long triageId,
    Long userId,
    Long sessionId,
    List<String> redFlags,
    String chiefComplaint,
    Instant occurredAt
) {
    public EmergencyCaseDetectedDomainEvent(Long triageId, Long userId, Long sessionId,
                                           List<String> redFlags, String chiefComplaint) {
        this(triageId, userId, sessionId, redFlags, chiefComplaint, Instant.now());
    }
}

