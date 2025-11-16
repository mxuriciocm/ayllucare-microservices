package com.microservice.triage.domain.model.queries;

/**
 * Query to get a triage result by its ID.
 */
public record GetTriageByIdQuery(Long triageId) {
    public GetTriageByIdQuery {
        if (triageId == null || triageId <= 0) {
            throw new IllegalArgumentException("Triage ID must be a positive number");
        }
    }
}

