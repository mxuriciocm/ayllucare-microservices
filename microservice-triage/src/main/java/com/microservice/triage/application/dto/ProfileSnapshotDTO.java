package com.microservice.triage.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Profile Snapshot from Profile microservice.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSnapshotDTO {
    private Long userId;
    private Integer age;
    private String bloodType;
    private List<String> chronicConditions;
    private List<String> allergies;
    private List<String> currentMedications;
    private Boolean isPregnant;
    private Boolean hasConsentForAI;
}
