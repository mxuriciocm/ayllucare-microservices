package com.microservice.triage.application.events;

import com.microservice.triage.domain.model.valueobjects.PriorityLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Event published when a triage result is created.
 * To be consumed by CaseDesk microservice.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageResultCreatedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long triageId;
    private Long userId;
    private Long sessionId;
    private PriorityLevel priority;
    private List<String> riskFactors;
    private List<String> redFlags;
    private String recommendations;

    public TriageResultCreatedEvent(Long triageId, Long userId, Long sessionId,
                                   PriorityLevel priority, List<String> riskFactors,
                                   List<String> redFlags, String recommendations) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = "TRIAGE_RESULT_CREATED";
        this.occurredAt = Instant.now();
        this.triageId = triageId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.priority = priority;
        this.riskFactors = riskFactors;
        this.redFlags = redFlags;
        this.recommendations = recommendations;
    }
}

