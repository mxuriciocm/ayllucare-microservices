package com.microservice.anamnesis.domain.services;

import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.domain.model.commands.AddMessageToSessionCommand;
import com.microservice.anamnesis.domain.model.commands.CancelAnamnesisSessionCommand;
import com.microservice.anamnesis.domain.model.commands.CompleteAnamnesisSessionCommand;
import com.microservice.anamnesis.domain.model.commands.StartAnamnesisSessionCommand;

import java.util.Optional;

/**
 * Command service interface for anamnesis session operations.
 * Defines write operations (commands) for the anamnesis bounded context.
 */
public interface AnamnesisCommandService {

    /**
     * Handle command to start a new anamnesis session
     */
    Optional<AnamnesisSession> handle(StartAnamnesisSessionCommand command);

    /**
     * Handle command to add a message to a session
     */
    Optional<AnamnesisSession> handle(AddMessageToSessionCommand command);

    /**
     * Handle command to complete an anamnesis session
     */
    Optional<AnamnesisSession> handle(CompleteAnamnesisSessionCommand command);

    /**
     * Handle command to cancel an anamnesis session
     */
    Optional<AnamnesisSession> handle(CancelAnamnesisSessionCommand command);
}


