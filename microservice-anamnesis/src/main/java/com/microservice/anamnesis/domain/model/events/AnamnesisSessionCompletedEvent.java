package com.microservice.anamnesis.domain.model.events;

import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisStatus;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSessionCompletedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long sessionId;
    private Long userId;
    private AnamnesisStatus status;

    public AnamnesisSessionCompletedEvent(Long sessionId, Long userId, AnamnesisStatus status) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = "ANAMNESIS_SESSION_COMPLETED";
        this.occurredAt = Instant.now();
        this.sessionId = sessionId;
        this.userId = userId;
        this.status = status;
    }
}

