package com.microservice.triage.domain.model.queries;

/**
 * Query to get all triage results for a specific user.
 */
public record GetTriageByUserIdQuery(Long userId) {
    public GetTriageByUserIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
}

