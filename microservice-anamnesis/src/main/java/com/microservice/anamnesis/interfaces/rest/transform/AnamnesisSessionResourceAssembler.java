package com.microservice.anamnesis.interfaces.rest.transform;

import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.interfaces.rest.resources.AnamnesisSessionResource;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Assembler to transform AnamnesisSession entity to REST resource.
 */
public class AnamnesisSessionResourceAssembler {

    public static AnamnesisSessionResource toResourceFromEntity(AnamnesisSession session) {
        return new AnamnesisSessionResource(
                session.getId(),
                session.getUserId(),
                session.getStatus(),
                session.getInitialReason(),
                Integer.valueOf(session.getMessages().size()),
                session.getSummary(),
                session.getCreatedAt() != null ? LocalDateTime.ofInstant(session.getCreatedAt().toInstant(), ZoneId.systemDefault()) : null,
                session.getUpdatedAt() != null ? LocalDateTime.ofInstant(session.getUpdatedAt().toInstant(), ZoneId.systemDefault()) : null
        );
    }
}

