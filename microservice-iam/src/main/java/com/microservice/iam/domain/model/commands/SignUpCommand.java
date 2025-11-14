package com.microservice.iam.domain.model.commands;

import com.microservice.iam.domain.model.entities.Role;

import java.util.List;

/**
 * SignUpCommand - Generic user registration command for AylluCare/B4U.
 * <p>
 *     Prefer using RegisterPatientCommand or RegisterDoctorCommand
 *     for more explicit domain semantics.
 * </p>
 *
 * @param firstName the user's first name
 * @param lastName the user's last name
 * @param email the user's email (used as username)
 * @param password the plaintext password (will be hashed)
 * @param roles the roles to assign
 * @param phoneNumber the user's phone number (optional)
 * @param preferredLanguage the user's preferred language (optional)
 */
public record SignUpCommand(
    String firstName,
    String lastName,
    String email,
    String password,
    List<Role> roles,
    String phoneNumber,
    String preferredLanguage
) {
}
