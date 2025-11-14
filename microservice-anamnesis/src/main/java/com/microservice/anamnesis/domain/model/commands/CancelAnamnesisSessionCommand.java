package com.microservice.anamnesis.domain.model.commands;

public record CancelAnamnesisSessionCommand(Long sessionId, Long userId, String reason) {
    public CancelAnamnesisSessionCommand {
        if (sessionId == null) throw new IllegalArgumentException("Session ID cannot be null");
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
    }
}

