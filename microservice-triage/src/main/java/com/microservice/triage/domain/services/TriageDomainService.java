package com.microservice.triage.domain.services;

import com.microservice.triage.application.dto.AnamnesisSummaryDTO;
import com.microservice.triage.application.dto.ProfileSnapshotDTO;
import com.microservice.triage.domain.model.valueobjects.PriorityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain Service for calculating triage priority based on medical rules.
 * Contains the core business logic for priority assessment.
 */
@Service
public class TriageDomainService {

    private static final Logger logger = LoggerFactory.getLogger(TriageDomainService.class);

    /**
     * Calculates the priority level based on anamnesis summary and patient profile.
     *
     * @param summary The anamnesis summary
     * @param profile The patient profile
     * @return The calculated priority level
     */
    public PriorityLevel calculatePriority(AnamnesisSummaryDTO summary, ProfileSnapshotDTO profile) {
        logger.info("Calculating triage priority for user");

        // Check for EMERGENCY conditions
        if (hasEmergencyConditions(summary, profile)) {
            logger.warn("EMERGENCY priority detected");
            return PriorityLevel.EMERGENCY;
        }

        // Check for HIGH priority conditions
        if (hasHighPriorityConditions(summary, profile)) {
            logger.info("HIGH priority detected");
            return PriorityLevel.HIGH;
        }

        // Check for MODERATE priority conditions
        if (hasModeratePriorityConditions(summary, profile)) {
            logger.info("MODERATE priority detected");
            return PriorityLevel.MODERATE;
        }

        // Default to LOW priority
        logger.info("LOW priority detected");
        return PriorityLevel.LOW;
    }

    /**
     * Identifies risk factors from the anamnesis and profile.
     *
     * @param summary The anamnesis summary
     * @param profile The patient profile
     * @return List of identified risk factors
     */
    public List<String> identifyRiskFactors(AnamnesisSummaryDTO summary, ProfileSnapshotDTO profile) {
        List<String> riskFactors = new ArrayList<>();

        // Chronic conditions
        if (profile != null && profile.getChronicConditions() != null && !profile.getChronicConditions().isEmpty()) {
            profile.getChronicConditions().forEach(condition ->
                riskFactors.add("Condición crónica: " + condition));
        }

        // Current medications (drug interactions)
        if (summary.getMedications() != null && !summary.getMedications().isEmpty()) {
            riskFactors.add("Paciente toma " + summary.getMedications().size() + " medicamento(s)");
        }

        // Allergies
        if (summary.getAllergies() != null && !summary.getAllergies().isEmpty()) {
            summary.getAllergies().forEach(allergy ->
                riskFactors.add("Alergia: " + allergy));
        }

        // Age risk factors (if available)
        if (profile != null && profile.getAge() != null) {
            if (profile.getAge() < 5) {
                riskFactors.add("Paciente pediátrico menor de 5 años");
            } else if (profile.getAge() > 65) {
                riskFactors.add("Paciente adulto mayor (>65 años)");
            }
        }

        return riskFactors;
    }

    /**
     * Generates medical recommendations based on priority and conditions.
     *
     * @param priority The calculated priority
     * @param summary The anamnesis summary
     * @param profile The patient profile
     * @return Medical recommendations
     */
    public String generateRecommendations(PriorityLevel priority, AnamnesisSummaryDTO summary, ProfileSnapshotDTO profile) {
        StringBuilder recommendations = new StringBuilder();

        switch (priority) {
            case EMERGENCY:
                recommendations.append("⚠️ ATENCIÓN MÉDICA INMEDIATA REQUERIDA:\n");
                recommendations.append("- Acudir a emergencias inmediatamente o llamar al 911/105\n");
                recommendations.append("- No conducir, solicitar ambulancia o transporte asistido\n");
                recommendations.append("- No ingerir alimentos ni medicamentos hasta evaluación médica\n");
                break;

            case HIGH:
                recommendations.append("🔴 ATENCIÓN MÉDICA URGENTE (dentro de las próximas horas):\n");
                recommendations.append("- Acudir a centro de salud u hospital en las próximas 2-4 horas\n");
                recommendations.append("- Monitorear síntomas de cerca\n");
                recommendations.append("- Preparar lista de medicamentos actuales y alergias\n");
                break;

            case MODERATE:
                recommendations.append("🟡 ATENCIÓN MÉDICA NECESARIA (24-48 horas):\n");
                recommendations.append("- Agendar cita médica en las próximas 24-48 horas\n");
                recommendations.append("- Mantener reposo relativo\n");
                recommendations.append("- Hidratación adecuada\n");
                break;

            case LOW:
                recommendations.append("🟢 CUIDADOS GENERALES:\n");
                recommendations.append("- Descanso adecuado\n");
                recommendations.append("- Hidratación\n");
                recommendations.append("- Consultar si los síntomas empeoran\n");
                break;
        }

        // Add allergy warning if applicable
        if (summary.getAllergies() != null && !summary.getAllergies().isEmpty()) {
            recommendations.append("\n⚠️ ALERGIAS CONOCIDAS: ");
            recommendations.append(String.join(", ", summary.getAllergies()));
            recommendations.append("\n- Informar al personal médico sobre alergias");
        }

        // Add chronic condition considerations
        if (profile != null && profile.getChronicConditions() != null && !profile.getChronicConditions().isEmpty()) {
            recommendations.append("\n📋 CONDICIONES CRÓNICAS: ");
            recommendations.append(String.join(", ", profile.getChronicConditions()));
        }

        return recommendations.toString();
    }

    // ========================================================================
    // PRIVATE CLINICAL RULES
    // ========================================================================

    private boolean hasEmergencyConditions(AnamnesisSummaryDTO summary, ProfileSnapshotDTO profile) {
        List<String> redFlags = summary.getRedFlags();
        if (redFlags == null || redFlags.isEmpty()) {
            return false;
        }

        String allContent = String.join(" ", redFlags).toLowerCase() + " " +
                           summary.getChiefComplaint().toLowerCase() + " " +
                           summary.getHistoryOfPresentIllness().toLowerCase();

        // Emergency conditions
        return containsAny(allContent,
            "dificultad respiratoria severa", "dificultad para respirar",
            "dolor torácico", "dolor en el pecho", "dolor de pecho",
            "pérdida de conciencia", "desmayo", "inconsciencia",
            "convulsiones", "convulsión",
            "sangrado abundante", "hemorragia",
            "visión borrosa", "pérdida de visión",
            "debilidad en un lado", "parálisis",
            "confusión severa", "desorientación",
            "sospecha de meningitis", "rigidez de nuca intensa"
        );
    }

    private boolean hasHighPriorityConditions(AnamnesisSummaryDTO summary, ProfileSnapshotDTO profile) {
        String allContent = summary.getChiefComplaint().toLowerCase() + " " +
                           summary.getHistoryOfPresentIllness().toLowerCase();

        // High fever
        if (containsAny(allContent, "fiebre alta", "39", "40", "41") &&
            containsAny(allContent, "escalofríos", "temblor")) {
            return true;
        }

        // Severe persistent pain
        if (containsAny(allContent, "dolor intenso", "dolor severo", "dolor persistente")) {
            return true;
        }

        // Neurological symptoms
        if (containsAny(allContent, "mareo intenso", "vértigo", "cefalea intensa", "dolor de cabeza intenso")) {
            return true;
        }

        // Pregnancy with warning signs
        if (profile != null && Boolean.TRUE.equals(profile.getIsPregnant()) &&
            containsAny(allContent, "sangrado", "dolor abdominal", "contracciones")) {
            return true;
        }

        return false;
    }

    private boolean hasModeratePriorityConditions(AnamnesisSummaryDTO summary, ProfileSnapshotDTO profile) {
        String allContent = summary.getChiefComplaint().toLowerCase() + " " +
                           summary.getHistoryOfPresentIllness().toLowerCase();

        // Moderate pain
        if (containsAny(allContent, "dolor moderado", "molestia")) {
            return true;
        }

        // Mild fever
        if (containsAny(allContent, "fiebre", "38", "38.5") &&
            !containsAny(allContent, "39", "40", "41")) {
            return true;
        }

        // Digestive issues
        if (containsAny(allContent, "vómito", "diarrea", "náuseas persistentes")) {
            return true;
        }

        // Multiple symptoms
        int symptomCount = 0;
        if (containsAny(allContent, "dolor")) symptomCount++;
        if (containsAny(allContent, "fiebre")) symptomCount++;
        if (containsAny(allContent, "náusea", "vómito")) symptomCount++;
        if (containsAny(allContent, "mareo")) symptomCount++;

        return symptomCount >= 2;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}

