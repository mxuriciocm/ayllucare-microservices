package com.microservice.casedesk.application.events;

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
    private Long sessionId; // Cambio de anamnesisSessionId a sessionId para coincidir con Triage
    private String priority; // Cambio de PriorityLevel a String para simplificar deserialización
    private List<String> riskFactors;
    private List<String> redFlags;
    private String recommendations; // Cambio de recommendedAction a recommendations

    // Propiedades adicionales derivadas del resumen de anamnesis (pueden ser null)
    private String chiefComplaint;

    // Getter de compatibilidad
    public Long getAnamnesisSessionId() {
        return sessionId;
    }

    public String getTriageLevel() {
        return priority;
    }

    public String getRecommendedAction() {
        return recommendations;
    }
}
