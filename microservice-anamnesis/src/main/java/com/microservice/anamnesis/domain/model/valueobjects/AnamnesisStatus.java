package com.microservice.anamnesis.domain.model.valueobjects;
/**
 * Enum representing the status of an anamnesis session.
 * <p>
 * Status lifecycle:
 * CREATED -> IN_PROGRESS -> COMPLETED
 *                       \-> CANCELLED
 * </p>
 */
public enum AnamnesisStatus {
    /**
     * Session has been created but conversation has not started yet
     */
    CREATED,
    /**
     * Conversation is actively ongoing
     */
    IN_PROGRESS,
    /**
     * Anamnesis session is complete with a finalized summary
     */
    COMPLETED,
    /**
     * Session was cancelled by user or system before completion
     */
    CANCELLED
}
