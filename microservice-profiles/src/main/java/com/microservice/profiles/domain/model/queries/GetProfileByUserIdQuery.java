package com.microservice.profiles.domain.model.queries;

/**
 * Get Profile By User ID Query
 * Retrieves a patient profile by its associated user ID from IAM service
 */
public record GetProfileByUserIdQuery(Long userId) {}

