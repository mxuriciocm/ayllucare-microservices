package com.microservice.iam.domain.model.commands;

/**
 * RegisterPatientCommand - Command for patient registration in AylluCare/B4U.
 * <p>
 *     Used when a rural user registers as a patient in the digital health platform.
 *     Automatically assigns ROLE_PATIENT.
 * </p>
 *
 * @param firstName the patient's first name
 * @param lastName the patient's last name
 * @param email the patient's email address (must be unique)
 * @param password the plaintext password (will be hashed)
 * @param phoneNumber the patient's phone number (optional)
 * @param preferredLanguage the patient's preferred language (e.g., "es", "qu", "en")
 */
public record RegisterPatientCommand(
    String firstName,
    String lastName,
    String email,
    String password,
    String phoneNumber,
    String preferredLanguage
) {}

