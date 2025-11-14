package com.microservice.anamnesis.domain.services;

import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.domain.model.queries.GetAllSessionsQuery;
import com.microservice.anamnesis.domain.model.queries.GetSessionByIdQuery;
import com.microservice.anamnesis.domain.model.queries.GetSessionsByUserIdAndStatusQuery;
import com.microservice.anamnesis.domain.model.queries.GetSessionsByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service interface for anamnesis session operations.
 * Defines read operations (queries) for the anamnesis bounded context.
 */
public interface AnamnesisQueryService {

    /**
     * Handle query to get a session by ID
     */
    Optional<AnamnesisSession> handle(GetSessionByIdQuery query);

    /**
     * Handle query to get all sessions for a user
     */
    List<AnamnesisSession> handle(GetSessionsByUserIdQuery query);

    /**
     * Handle query to get sessions by user ID and status
     */
    List<AnamnesisSession> handle(GetSessionsByUserIdAndStatusQuery query);

    /**
     * Handle query to get all sessions
     */
    List<AnamnesisSession> handle(GetAllSessionsQuery query);
}

