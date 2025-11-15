package com.microservice.profiles.interfaces.rest.resources;

/**
 * Resource for creating a new patient profile
 */
public record CreateProfileResource(
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String dateOfBirth,
        String gender,
        String bloodType,
        Double height,
        Double weight,
        String[] allergies,
        String[] chronicConditions,
        String[] currentMedications,
        String emergencyContactName,
        String emergencyContactPhone,
        String emergencyContactRelationship,
        Boolean consentForDataSharing,
        Boolean consentForAIProcessing
) {
}

