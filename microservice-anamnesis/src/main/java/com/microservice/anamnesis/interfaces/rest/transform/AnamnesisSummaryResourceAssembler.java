package com.microservice.anamnesis.interfaces.rest.transform;

import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.interfaces.rest.resources.AnamnesisSummaryResource;

/**
 * Assembler to transform AnamnesisSession to AnamnesisSummaryResource.
 */
public class AnamnesisSummaryResourceAssembler {

    public static AnamnesisSummaryResource toResourceFromEntity(AnamnesisSession session) {
        return new AnamnesisSummaryResource(
                session.getId(),
                session.getUserId(),
                session.getSummary()
        );
    }
}

