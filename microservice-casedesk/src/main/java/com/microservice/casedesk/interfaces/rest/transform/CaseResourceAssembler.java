package com.microservice.casedesk.interfaces.rest.transform;

import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.interfaces.rest.resources.CaseResource;

public class CaseResourceAssembler {

    public static CaseResource toResourceFromEntity(Case entity) {
        return new CaseResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getAnamnesisSessionId(),
                entity.getTriageLevel(),
                entity.getChiefComplaint(),
                entity.getStatus(),
                entity.getAssignedDoctorId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
