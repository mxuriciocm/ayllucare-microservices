package com.microservice.profiles.domain.model.commands;

/**
 * Sign Consent Command for AylluCare
 * Patient signs consent for data sharing and AI processing (GDPR/HIPAA compliance)
 * @param userId User ID from IAM
 */
public record SignConsentCommand(Long userId) {
}

