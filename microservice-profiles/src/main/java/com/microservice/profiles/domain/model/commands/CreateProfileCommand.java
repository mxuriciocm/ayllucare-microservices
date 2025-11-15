package com.microservice.profiles.domain.model.commands;

import java.time.LocalDate;
import java.util.List;

/**
 * Create User Profile Command
 * @param userId ID of the user this profile belongs to
 * @param firstName First name
 * @param lastName Last name
 * @param phoneNumber Phone number
 * @param dateOfBirth Date of birth
 * @param gender Gender
 * @param bloodType Blood type
 * @param height Height in cm
 * @param weight Weight in kg
 * @param allergies List of allergies
 * @param chronicConditions List of chronic conditions
 * @param currentMedications List of current medications
 * @param emergencyContactName Emergency contact name
 * @param emergencyContactPhone Emergency contact phone
 * @param emergencyContactRelationship Emergency contact relationship
 * @param consentForDataSharing Consent for data sharing
 * @param consentForAIProcessing Consent for AI processing
 */
public record CreateProfileCommand(
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String gender,
        String bloodType,
        Double height,
        Double weight,
        List<String> allergies,
        List<String> chronicConditions,
        List<String> currentMedications,
        String emergencyContactName,
        String emergencyContactPhone,
        String emergencyContactRelationship,
        Boolean consentForDataSharing,
        Boolean consentForAIProcessing
) {}
