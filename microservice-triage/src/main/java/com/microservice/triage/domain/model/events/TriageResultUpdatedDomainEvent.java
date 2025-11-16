package com.microservice.triage.domain.model.events;

import com.microservice.triage.domain.model.valueobjects.PriorityLevel;

import java.time.Instant;

/**
 * Domain Event: Triage Result Updated.
 * Published when a triage result priority is updated.
 * This is a domain-level event (internal to the microservice).
 */
public record TriageResultUpdatedDomainEvent(
    Long triageId,
    Long userId,
    PriorityLevel oldPriority,
    PriorityLevel newPriority,
    String reason,
    Instant occurredAt
) {
    public TriageResultUpdatedDomainEvent(Long triageId, Long userId,
                                         PriorityLevel oldPriority, PriorityLevel newPriority,
                                         String reason) {
        this(triageId, userId, oldPriority, newPriority, reason, Instant.now());
    }
}

