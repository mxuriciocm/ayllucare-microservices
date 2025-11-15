package com.microservice.triage.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Anamnesis Summary received from Anamnesis-LLM microservice.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSummaryDTO {
    private String chiefComplaint;
    private String historyOfPresentIllness;
    private String pastMedicalHistory;
    private List<String> medications;
    private List<String> allergies;
    private List<String> redFlags;
    private String additionalNotes;
}

