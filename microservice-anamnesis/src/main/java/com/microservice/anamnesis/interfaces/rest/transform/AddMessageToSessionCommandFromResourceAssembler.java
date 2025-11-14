package com.microservice.anamnesis.interfaces.rest.transform;

import com.microservice.anamnesis.domain.model.commands.AddMessageToSessionCommand;
import com.microservice.anamnesis.interfaces.rest.resources.AddMessageResource;

/**
 * Assembler to transform AddMessageResource to AddMessageToSessionCommand.
 */
public class AddMessageToSessionCommandFromResourceAssembler {

    public static AddMessageToSessionCommand toCommandFromResource(Long sessionId, Long userId, AddMessageResource resource) {
        return new AddMessageToSessionCommand(
                sessionId,
                userId,
                resource.getContent()
        );
    }
}

