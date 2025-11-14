package com.microservice.anamnesis.domain.model.commands;

/**
 * Command to start a new anamnesis session for a patient.
 *
 * @param userId User ID from IAM service
 * @param initialReason Optional initial reason for consultation
 */
public record StartAnamnesisSessionCommand(Long userId, String initialReason) {
    public StartAnamnesisSessionCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}


