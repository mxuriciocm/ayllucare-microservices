package com.microservice.casedesk.domain.model.events;

import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.domain.model.valueobjects.TriageLevel;
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
public class CaseCreatedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long caseId;
    private Long patientId;
    private Long anamnesisSessionId;
    private TriageLevel triageLevel;
    private CaseStatus status;
    private String chiefComplaint;

    public static CaseCreatedEvent create(Long caseId, Long patientId, Long anamnesisSessionId,
                                          TriageLevel triageLevel, String chiefComplaint) {
        return CaseCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("CASE_CREATED")
                .occurredAt(Instant.now())
                .caseId(caseId)
                .patientId(patientId)
                .anamnesisSessionId(anamnesisSessionId)
                .triageLevel(triageLevel)
                .status(CaseStatus.OPEN)
                .chiefComplaint(chiefComplaint)
                .build();
    }
}
