package com.microservice.profiles.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Profile Resource for AylluCare
 * Represents a patient profile with medical and personal information
 */
public record ProfileResource(
    Long id,
    Long userId,
    String fullName,
    String phoneNumber,
    String address,
    String emergencyContactName,
    String emergencyContactPhone,
    LocalDate dateOfBirth,
    String bloodType,
    Double heightCm,
    Double weightKg,
    Double bmi,
    List<String> allergies,
    List<String> chronicConditions,
    List<String> currentMedications,
    Boolean consentForDataSharing,
    Boolean consentForAIProcessing,
    LocalDateTime consentSignedAt
) {}
