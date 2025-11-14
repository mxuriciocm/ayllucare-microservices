package com.microservice.anamnesis.domain.model.valueobjects;
/**
 * Enum representing the sender type of a conversation message.
 */
public enum SenderType {
    /**
     * Message from the patient
     */
    PATIENT,
    /**
     * Message from the AI assistant (LLM)
     */
    ASSISTANT,
    /**
     * System-generated message (e.g., session start, errors)
     */
    SYSTEM
}
