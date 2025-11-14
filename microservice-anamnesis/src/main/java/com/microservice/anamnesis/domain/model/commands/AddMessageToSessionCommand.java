package com.microservice.anamnesis.domain.model.commands;

public record AddMessageToSessionCommand(Long sessionId, Long userId, String content) {
    public AddMessageToSessionCommand {
        if (sessionId == null) throw new IllegalArgumentException("Session ID cannot be null");
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Message content cannot be empty");
    }
}

