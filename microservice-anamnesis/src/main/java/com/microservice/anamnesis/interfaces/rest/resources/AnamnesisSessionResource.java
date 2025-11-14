package com.microservice.anamnesis.interfaces.rest.resources;

import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisStatus;
import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * REST resource representing an anamnesis session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSessionResource {
    private Long id;
    private Long userId;
    private AnamnesisStatus status;
    private String initialReason;
    private Integer messageCount;
    private AnamnesisSummary summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

