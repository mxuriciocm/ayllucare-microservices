package com.microservice.triage.domain.model.commands;

import com.microservice.triage.domain.model.valueobjects.PriorityLevel;

import java.util.List;

/**
 * Command to create a new triage result.
 */
public record CreateTriageResultCommand(
    Long userId,
    Long sessionId,
    PriorityLevel priority,
    List<String> riskFactors,
    List<String> redFlagsDetected,
    String recommendations
) {
    public CreateTriageResultCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        if (sessionId == null || sessionId <= 0) {
            throw new IllegalArgumentException("Session ID must be a positive number");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority level cannot be null");
        }
    }
}

