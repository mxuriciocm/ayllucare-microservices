package com.microservice.anamnesis.interfaces.rest.transform;

import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.interfaces.rest.resources.AnamnesisSessionDetailResource;

/**
 * Assembler to transform AnamnesisSession entity to detailed REST resource with messages.
 */
public class AnamnesisSessionDetailResourceAssembler {

    public static AnamnesisSessionDetailResource toResourceFromEntity(AnamnesisSession session) {
        return new AnamnesisSessionDetailResource(
                AnamnesisSessionResourceAssembler.toResourceFromEntity(session),
                session.getMessages()
        );
    }
}

