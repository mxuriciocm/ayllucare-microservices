package com.microservice.anamnesis.domain.model.queries;

public record GetSessionByIdQuery(Long sessionId) {
    public GetSessionByIdQuery {
        if (sessionId == null) throw new IllegalArgumentException("Session ID cannot be null");
    }
}

