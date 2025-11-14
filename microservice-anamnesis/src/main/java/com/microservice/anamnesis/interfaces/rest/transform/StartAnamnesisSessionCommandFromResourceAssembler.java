package com.microservice.anamnesis.interfaces.rest.transform;

import com.microservice.anamnesis.domain.model.commands.StartAnamnesisSessionCommand;
import com.microservice.anamnesis.interfaces.rest.resources.CreateAnamnesisSessionResource;

/**
 * Assembler to transform CreateAnamnesisSessionResource to StartAnamnesisSessionCommand.
 */
public class StartAnamnesisSessionCommandFromResourceAssembler {

    public static StartAnamnesisSessionCommand toCommandFromResource(Long userId, CreateAnamnesisSessionResource resource) {
        return new StartAnamnesisSessionCommand(
                userId,
                resource.getInitialReason()
        );
    }
}

