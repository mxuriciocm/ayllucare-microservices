package com.microservice.anamnesis.domain.model.valueobjects;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSummary {

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(name = "history_of_present_illness", columnDefinition = "TEXT")
    private String historyOfPresentIllness;

    @Column(name = "past_medical_history", columnDefinition = "TEXT")
    private String pastMedicalHistory;

    @ElementCollection
    @CollectionTable(name = "anamnesis_summary_medications", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "medication")
    private List<String> medications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "anamnesis_summary_allergies", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "allergy")
    private List<String> allergies = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "anamnesis_summary_red_flags", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "red_flag")
    private List<String> redFlags = new ArrayList<>();

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    public static AnamnesisSummary empty() {
        return new AnamnesisSummary("", "", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "");
    }

    public boolean isEmpty() {
        return (chiefComplaint == null || chiefComplaint.isBlank()) &&
               (historyOfPresentIllness == null || historyOfPresentIllness.isBlank());
    }
}

