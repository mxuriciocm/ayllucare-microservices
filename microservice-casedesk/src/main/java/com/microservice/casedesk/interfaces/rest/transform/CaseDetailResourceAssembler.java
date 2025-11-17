package com.microservice.casedesk.interfaces.rest.transform;

import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.interfaces.rest.resources.CaseDetailResource;

public class CaseDetailResourceAssembler {

    public static CaseDetailResource toResourceFromEntity(Case entity) {
        return new CaseDetailResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getAnamnesisSessionId(),
                entity.getTriageLevel(),
                entity.getChiefComplaint(),
                entity.getMainRedFlags(),
                entity.getStatus(),
                entity.getAssignedDoctorId(),
                entity.getNotes(),
                entity.getTriageRecommendedAction(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getClosedAt()
        );
    }
}
