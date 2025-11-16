package com.microservice.triage.interfaces.rest.transform;

import com.microservice.triage.domain.model.aggregates.TriageResult;
import com.microservice.triage.interfaces.rest.resources.TriageResultResource;

/**
 * Assembler to convert TriageResult entity to TriageResultResource DTO.
 */
public class TriageResultResourceAssembler {

    /**
     * Converts a TriageResult entity to a TriageResultResource DTO.
     *
     * @param entity The TriageResult entity
     * @return The TriageResultResource DTO
     */
    public static TriageResultResource toResourceFromEntity(TriageResult entity) {
        return new TriageResultResource(
            entity.getId(),
            entity.getUserId(),
            entity.getSessionId(),
            entity.getPriority(),
            entity.getRiskFactors(),
            entity.getRedFlagsDetected(),
            entity.getRecommendations(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

