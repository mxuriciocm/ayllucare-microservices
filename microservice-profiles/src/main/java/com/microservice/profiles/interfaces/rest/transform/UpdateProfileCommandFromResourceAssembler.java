package com.microservice.profiles.interfaces.rest.transform;

import com.microservice.profiles.domain.model.commands.UpdateProfileCommand;
import com.microservice.profiles.interfaces.rest.resources.UpdateProfileResource;

/**
 * Assembler to transform UpdateProfileResource DTO to UpdateProfileCommand
 */
public class UpdateProfileCommandFromResourceAssembler {

    public static UpdateProfileCommand toCommandFromResource(UpdateProfileResource resource, Long profileId) {
        return new UpdateProfileCommand(
            profileId,
            resource.firstName(),
            resource.lastName(),
            resource.phoneNumber(),
            resource.address(),
            resource.emergencyContactName(),
            resource.emergencyContactPhone(),
            resource.dateOfBirth(),
            resource.bloodType(),
            resource.heightCm(),
            resource.weightKg(),
            resource.allergies(),
            resource.chronicConditions(),
            resource.currentMedications()
        );
    }
}
