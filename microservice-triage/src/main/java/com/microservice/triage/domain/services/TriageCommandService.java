package com.microservice.triage.domain.services;

import com.microservice.triage.domain.model.aggregates.TriageResult;
import com.microservice.triage.domain.model.commands.CreateTriageResultCommand;
import com.microservice.triage.domain.model.queries.GetAllTriagesQuery;
import com.microservice.triage.domain.model.queries.GetTriageByIdQuery;
import com.microservice.triage.domain.model.queries.GetTriageBySessionIdQuery;
import com.microservice.triage.domain.model.queries.GetTriageByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Triage Command Service Interface.
 * Handles write operations for triage results.
 */
public interface TriageCommandService {

    /**
     * Creates a new triage result.
     *
     * @param command The create triage command
     * @return The created triage result
     */
    Optional<TriageResult> handle(CreateTriageResultCommand command);
}

