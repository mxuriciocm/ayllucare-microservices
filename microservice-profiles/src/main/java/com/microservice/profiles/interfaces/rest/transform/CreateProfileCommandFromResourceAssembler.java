package com.microservice.profiles.interfaces.rest.transform;

import com.microservice.profiles.domain.model.commands.CreateProfileCommand;
import com.microservice.profiles.interfaces.rest.resources.CreateProfileResource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CreateProfileCommandFromResourceAssembler {
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource, Long userId) {
        LocalDate dateOfBirth = resource.dateOfBirth() != null ? LocalDate.parse(resource.dateOfBirth()) : null;
        List<String> allergies = resource.allergies() != null ? Arrays.asList(resource.allergies()) : List.of();
        List<String> chronicConditions = resource.chronicConditions() != null ? Arrays.asList(resource.chronicConditions()) : List.of();
        List<String> currentMedications = resource.currentMedications() != null ? Arrays.asList(resource.currentMedications()) : List.of();

        return new CreateProfileCommand(
                userId,                                          // 1. userId
                resource.firstName(),                            // 2. firstName
                resource.lastName(),                             // 3. lastName
                resource.phoneNumber(),                          // 4. phoneNumber
                dateOfBirth,                                     // 5. dateOfBirth
                resource.gender(),                               // 6. gender
                resource.bloodType(),                            // 7. bloodType
                resource.height(),                               // 8. height
                resource.weight(),                               // 9. weight
                allergies,                                       // 10. allergies
                chronicConditions,                               // 11. chronicConditions
                currentMedications,                              // 12. currentMedications
                resource.emergencyContactName(),                 // 13. emergencyContactName
                resource.emergencyContactPhone(),                // 14. emergencyContactPhone
                resource.emergencyContactRelationship(),         // 15. emergencyContactRelationship
                resource.consentForDataSharing(),                // 16. consentForDataSharing
                resource.consentForAIProcessing()                // 17. consentForAIProcessing
        );
    }
}
