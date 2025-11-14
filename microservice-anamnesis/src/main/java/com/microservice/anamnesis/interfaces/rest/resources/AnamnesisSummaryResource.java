package com.microservice.anamnesis.interfaces.rest.resources;

import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST resource representing an anamnesis summary.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSummaryResource {
    private Long sessionId;
    private Long userId;
    private AnamnesisSummary summary;
}

