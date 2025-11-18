package com.microservice.casedesk.application.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageResultCreatedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long triageId;
    private Long userId;
    private Long sessionId;

    // Acepta tanto String como objeto con name/value
    @JsonProperty("priority")
    private Object priorityRaw;

    private List<String> riskFactors;
    private List<String> redFlags;
    private String recommendations;
    private String chiefComplaint;

    // Getter de compatibilidad para sessionId
    public Long getAnamnesisSessionId() {
        return sessionId;
    }

    // Getter que extrae el String del priority, sin importar si es String o Enum
    public String getTriageLevel() {
        if (priorityRaw == null) {
            return null;
        }
        // Si es un String directo
        if (priorityRaw instanceof String) {
            return (String) priorityRaw;
        }
        // Si es un objeto (enum serializado), intentar extraer el valor
        return priorityRaw.toString();
    }

    public String getRecommendedAction() {
        return recommendations;
    }
}
