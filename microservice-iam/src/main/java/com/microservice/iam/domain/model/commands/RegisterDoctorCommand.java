package com.microservice.iam.domain.model.commands;

/**
 * RegisterDoctorCommand - Command for doctor registration in AylluCare/B4U.
 * <p>
 *     Used when a health professional registers as a doctor in the platform.
 *     Automatically assigns ROLE_DOCTOR.
 *     Can be used for self-registration or admin-assisted onboarding.
 * </p>
 *
 * @param firstName the doctor's first name
 * @param lastName the doctor's last name
 * @param email the doctor's email address (must be unique)
 * @param password the plaintext password (will be hashed)
 * @param phoneNumber the doctor's phone number (optional)
 * @param requiresVerification if true, account starts as INACTIVE until verified
 */
public record RegisterDoctorCommand(
    String firstName,
    String lastName,
    String email,
    String password,
    String phoneNumber,
    boolean requiresVerification
) {}

