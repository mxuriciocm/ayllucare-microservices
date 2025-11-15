package com.microservice.profiles.interfaces.rest.transform;

import com.microservice.profiles.domain.model.commands.CreateProfileCommand;
import com.microservice.profiles.interfaces.rest.resources.CreateProfileResource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CreateProfileCommandFromResourceAssembler {
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        LocalDate dateOfBirth = resource.dateOfBirth() != null ? LocalDate.parse(resource.dateOfBirth()) : null;
        List<String> allergies = resource.allergies() != null ? Arrays.asList(resource.allergies()) : List.of();
        List<String> chronicConditions = resource.chronicConditions() != null ? Arrays.asList(resource.chronicConditions()) : List.of();
        List<String> currentMedications = resource.currentMedications() != null ? Arrays.asList(resource.currentMedications()) : List.of();

        return new CreateProfileCommand(
                resource.userId(),
                resource.firstName(),
                resource.lastName(),
                resource.phoneNumber(),
                dateOfBirth,
                resource.gender(),
                resource.bloodType(),
                resource.height(),
                resource.weight(),
                allergies,
                chronicConditions,
                currentMedications,
                resource.emergencyContactName(),
                resource.emergencyContactPhone(),
                resource.emergencyContactRelationship(),
                resource.consentForDataSharing(),
                resource.consentForAIProcessing()
        );
    }
}
