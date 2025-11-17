package com.microservice.casedesk.application.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageResultCreatedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long userId;
    private Long anamnesisSessionId;
    private String triageLevel;
    private String recommendedAction;
}
