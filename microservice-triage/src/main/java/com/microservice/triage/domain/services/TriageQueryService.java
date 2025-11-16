package com.microservice.triage.domain.services;

import com.microservice.triage.domain.model.aggregates.TriageResult;
import com.microservice.triage.domain.model.queries.GetAllTriagesQuery;
import com.microservice.triage.domain.model.queries.GetTriageByIdQuery;
import com.microservice.triage.domain.model.queries.GetTriageBySessionIdQuery;
import com.microservice.triage.domain.model.queries.GetTriageByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Triage Query Service Interface.
 * Handles read operations for triage results.
 */
public interface TriageQueryService {

    /**
     * Gets a triage result by ID.
     *
     * @param query The query with triage ID
     * @return The triage result if found
     */
    Optional<TriageResult> handle(GetTriageByIdQuery query);

    /**
     * Gets all triage results for a specific user.
     *
     * @param query The query with user ID
     * @return List of triage results for the user
     */
    List<TriageResult> handle(GetTriageByUserIdQuery query);

    /**
     * Gets a triage result by anamnesis session ID.
     *
     * @param query The query with session ID
     * @return The triage result if found
     */
    Optional<TriageResult> handle(GetTriageBySessionIdQuery query);

    /**
     * Gets all triage results (admin only).
     *
     * @param query The query
     * @return List of all triage results
     */
    List<TriageResult> handle(GetAllTriagesQuery query);
}

