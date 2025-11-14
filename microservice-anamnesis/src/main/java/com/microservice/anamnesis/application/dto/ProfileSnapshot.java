package com.microservice.anamnesis.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSnapshot {
    private Long userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String bloodType;
    private Double heightCm;
    private Double weightKg;
    private List<String> allergies;
    private List<String> chronicConditions;
    private List<String> currentMedications;
    private Boolean consentForAIProcessing;

    public boolean hasConsentForAI() {
        return Boolean.TRUE.equals(consentForAIProcessing);
    }

    public String getFormattedSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Información del paciente:\n");
        summary.append(String.format("- Nombre: %s %s\n", firstName, lastName));

        if (dateOfBirth != null) {
            summary.append(String.format("- Fecha de nacimiento: %s\n", dateOfBirth));
        }
        if (bloodType != null && !bloodType.isBlank()) {
            summary.append(String.format("- Tipo de sangre: %s\n", bloodType));
        }
        if (allergies != null && !allergies.isEmpty()) {
            summary.append(String.format("- Alergias conocidas: %s\n", String.join(", ", allergies)));
        }
        if (chronicConditions != null && !chronicConditions.isEmpty()) {
            summary.append(String.format("- Condiciones crónicas: %s\n", String.join(", ", chronicConditions)));
        }
        if (currentMedications != null && !currentMedications.isEmpty()) {
            summary.append(String.format("- Medicamentos actuales: %s\n", String.join(", ", currentMedications)));
        }

        return summary.toString();
    }
}

