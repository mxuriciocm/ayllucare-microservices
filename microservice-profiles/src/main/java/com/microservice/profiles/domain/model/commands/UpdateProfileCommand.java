package com.microservice.profiles.domain.model.commands;

import java.time.LocalDate;
import java.util.List;

/**
 * Update Profile Command for AylluCare
 * Updates patient medical and personal information
 * @param profileId Profile ID to update
 * @param firstName First name (optional)
 * @param lastName Last name (optional)
 * @param phoneNumber Phone number (optional)
 * @param address Address (optional)
 * @param emergencyContactName Emergency contact name (optional)
 * @param emergencyContactPhone Emergency contact phone (optional)
 * @param dateOfBirth Date of birth (optional)
 * @param bloodType Blood type (optional)
 * @param heightCm Height in cm (optional)
 * @param weightKg Weight in kg (optional)
 * @param allergies List of allergies (optional)
 * @param chronicConditions List of chronic conditions (optional)
 * @param currentMedications List of current medications (optional)
 */
public record UpdateProfileCommand(
    Long profileId,
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
