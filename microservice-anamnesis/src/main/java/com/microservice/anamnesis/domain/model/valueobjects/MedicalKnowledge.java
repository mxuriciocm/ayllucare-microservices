package com.microservice.anamnesis.domain.model.valueobjects;

import java.util.List;

/**
 * Value object representing medical knowledge retrieved from Ayllucare dataset.
 */
public record MedicalKnowledge(
        String topic,
        String description,
        List<String> symptoms,
        List<String> recommendations,
        List<String> redFlags,
        double relevanceScore
) {
    public static MedicalKnowledge empty() {
        return new MedicalKnowledge("", "", List.of(), List.of(), List.of(), 0.0);
    }

    public boolean isEmpty() {
        return topic == null || topic.isBlank();
    }
}



