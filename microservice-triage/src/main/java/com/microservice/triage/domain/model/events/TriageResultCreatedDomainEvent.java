package com.microservice.triage.domain.model.events;

import com.microservice.triage.domain.model.valueobjects.PriorityLevel;

import java.time.Instant;
import java.util.List;

/**
 * Domain Event: Triage Result Created.
 * Published when a new triage result is created in the domain.
 * This is a domain-level event (internal to the microservice).
 */
public record TriageResultCreatedDomainEvent(
    Long triageId,
    Long userId,
    Long sessionId,
    PriorityLevel priority,
    List<String> riskFactors,
    List<String> redFlags,
    Instant occurredAt
) {
    public TriageResultCreatedDomainEvent(Long triageId, Long userId, Long sessionId,
                                         PriorityLevel priority, List<String> riskFactors,
                                         List<String> redFlags) {
        this(triageId, userId, sessionId, priority, riskFactors, redFlags, Instant.now());
    }
}

