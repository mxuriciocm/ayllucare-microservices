package com.microservice.triage.application.events;

import com.microservice.triage.application.dto.AnamnesisSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event received from Anamnesis-LLM microservice when a summary is created.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSummaryCreatedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long sessionId;
    private Long userId;
    private AnamnesisSummaryDTO summary;
}

