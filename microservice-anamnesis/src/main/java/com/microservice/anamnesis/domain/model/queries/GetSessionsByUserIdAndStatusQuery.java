package com.microservice.anamnesis.domain.model.queries;

import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisStatus;

public record GetSessionsByUserIdAndStatusQuery(Long userId, AnamnesisStatus status) {
    public GetSessionsByUserIdAndStatusQuery {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
    }
}

