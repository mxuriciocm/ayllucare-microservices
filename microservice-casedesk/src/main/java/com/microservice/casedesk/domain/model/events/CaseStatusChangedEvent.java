package com.microservice.casedesk.domain.model.events;

import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseStatusChangedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long caseId;
    private Long patientId;
    private CaseStatus previousStatus;
    private CaseStatus newStatus;
    private Long changedByUserId;

    public static CaseStatusChangedEvent create(Long caseId, Long patientId,
                                                CaseStatus previousStatus, CaseStatus newStatus,
                                                Long changedByUserId) {
        return CaseStatusChangedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("CASE_STATUS_CHANGED")
                .occurredAt(Instant.now())
                .caseId(caseId)
                .patientId(patientId)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .changedByUserId(changedByUserId)
                .build();
    }
}
