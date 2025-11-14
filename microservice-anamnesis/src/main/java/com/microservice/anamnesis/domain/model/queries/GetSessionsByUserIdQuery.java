package com.microservice.anamnesis.domain.model.queries;

public record GetSessionsByUserIdQuery(Long userId) {
    public GetSessionsByUserIdQuery {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
    }
}

