package com.microservice.triage.domain.model.queries;

/**
 * Query to get a triage result by anamnesis session ID.
 */
public record GetTriageBySessionIdQuery(Long sessionId) {
    public GetTriageBySessionIdQuery {
        if (sessionId == null || sessionId <= 0) {
            throw new IllegalArgumentException("Session ID must be a positive number");
        }
    }
}
