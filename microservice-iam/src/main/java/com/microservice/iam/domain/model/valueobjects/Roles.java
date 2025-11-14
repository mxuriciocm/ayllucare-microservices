package com.microservice.iam.domain.model.valueobjects;

/**
 * Roles enum for AylluCare/B4U platform.
 * <p>
 *     This enum represents the three roles in the rural digital health platform:
 *     - ROLE_PATIENT: Rural users who interact with AI for anamnesis
 *     - ROLE_DOCTOR: Health professionals who review clinical cases
 *     - ROLE_ADMIN: System administrators
 * </p>
 */
public enum Roles {
    ROLE_PATIENT,
    ROLE_DOCTOR,
    ROLE_ADMIN
}

