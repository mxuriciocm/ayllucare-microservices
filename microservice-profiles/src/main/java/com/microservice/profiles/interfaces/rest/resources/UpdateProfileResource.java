package com.microservice.profiles.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource for updating a patient's profile in AylluCare.
 * All fields are optional - only provided fields will be updated.
 */
public record UpdateProfileResource(
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    String emergencyContactName,
    String emergencyContactPhone,
    LocalDate dateOfBirth,
    String bloodType,
    Double heightCm,
    Double weightKg,
    List<String> allergies,
    List<String> chronicConditions,
    List<String> currentMedications
) {}
