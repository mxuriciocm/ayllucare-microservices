package com.microservice.triage.interfaces.rest.resources;

import com.microservice.triage.domain.model.valueobjects.PriorityLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resource DTO for Triage Result.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageResultResource {
    private Long id;
    private Long userId;
    private Long sessionId;
    private PriorityLevel priority;
    private List<String> riskFactors;
    private List<String> redFlagsDetected;
    private String recommendations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

