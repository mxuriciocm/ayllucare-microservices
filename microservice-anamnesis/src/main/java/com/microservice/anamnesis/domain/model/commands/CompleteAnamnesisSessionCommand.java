package com.microservice.anamnesis.domain.model.commands;

public record CompleteAnamnesisSessionCommand(Long sessionId, Long userId) {
    public CompleteAnamnesisSessionCommand {
        if (sessionId == null) throw new IllegalArgumentException("Session ID cannot be null");
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
    }
}

