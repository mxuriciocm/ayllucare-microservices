package com.microservice.profiles.interfaces.rest.transform;

import com.microservice.profiles.domain.model.aggregates.Profile;
import com.microservice.profiles.interfaces.rest.resources.ProfileResource;

/**
 * Assembler to transform Profile entity to ProfileResource DTO
 */
public class ProfileResourceFromEntityAssembler {
    public static ProfileResource toResourceFromEntity(Profile entity) {
        return new ProfileResource(
            entity.getId(),
            entity.getUserId(),
            entity.getFullName(),
            entity.getPhoneNumber(),
            entity.getAddress(),
            entity.getEmergencyContactName(),
            entity.getEmergencyContactPhone(),
            entity.getDateOfBirth(),
            entity.getBloodType(),
            entity.getHeightCm(),
            entity.getWeightKg(),
            entity.calculateBMI(),
            entity.getAllergies(),
            entity.getChronicConditions(),
            entity.getCurrentMedications(),
            entity.getConsentForDataSharing(),
            entity.getConsentForAIProcessing(),
            entity.getConsentSignedAt()
        );
    }
}
