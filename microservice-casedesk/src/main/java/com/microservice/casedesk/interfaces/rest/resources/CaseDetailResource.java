package com.microservice.casedesk.interfaces.rest.resources;

import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.domain.model.valueobjects.TriageLevel;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record CaseDetailResource(
        Long id,
        Long patientId,
        Long anamnesisSessionId,
        TriageLevel triageLevel,
        String chiefComplaint,
        List<String> mainRedFlags,
        CaseStatus status,
        Long assignedDoctorId,
        List<String> notes,
        String triageRecommendedAction,
        Date createdAt,
        Date updatedAt,
        LocalDateTime closedAt
) {}
