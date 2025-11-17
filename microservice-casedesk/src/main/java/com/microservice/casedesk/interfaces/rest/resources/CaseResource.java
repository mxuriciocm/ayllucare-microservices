package com.microservice.casedesk.interfaces.rest.resources;

import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.domain.model.valueobjects.TriageLevel;

import java.time.LocalDateTime;
import java.util.Date;

public record CaseResource(
        Long id,
        Long patientId,
        Long anamnesisSessionId,
        TriageLevel triageLevel,
        String chiefComplaint,
        CaseStatus status,
        Long assignedDoctorId,
        Date createdAt,
        Date updatedAt
) {}
