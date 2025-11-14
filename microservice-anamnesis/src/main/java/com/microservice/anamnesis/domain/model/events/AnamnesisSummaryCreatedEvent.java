package com.microservice.anamnesis.domain.model.events;

import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisSummary;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSummaryCreatedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long sessionId;
    private Long userId;
    private AnamnesisSummary summary;

    public AnamnesisSummaryCreatedEvent(Long sessionId, Long userId, AnamnesisSummary summary) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = "ANAMNESIS_SUMMARY_CREATED";
        this.occurredAt = Instant.now();
        this.sessionId = sessionId;
        this.userId = userId;
        this.summary = summary;
    }
}

