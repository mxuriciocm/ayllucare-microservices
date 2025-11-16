package com.microservice.casedesk.domain.model.events;

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
public class CaseAssignedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long caseId;
    private Long patientId;
    private Long assignedDoctorId;
    private Long assignedByUserId;

    public static CaseAssignedEvent create(Long caseId, Long patientId,
                                           Long assignedDoctorId, Long assignedByUserId) {
        return CaseAssignedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("CASE_ASSIGNED")
                .occurredAt(Instant.now())
                .caseId(caseId)
                .patientId(patientId)
                .assignedDoctorId(assignedDoctorId)
                .assignedByUserId(assignedByUserId)
                .build();
    }
}
