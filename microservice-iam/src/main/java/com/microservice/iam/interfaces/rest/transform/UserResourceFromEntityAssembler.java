package com.microservice.iam.interfaces.rest.transform;

import com.microservice.iam.domain.model.aggregates.User;
import com.microservice.iam.interfaces.rest.resources.UserResource;

/**
 * Assembler to convert User entity to UserResource.
 */
public class UserResourceFromEntityAssembler {

    public static UserResource toResourceFromEntity(User entity) {
        return new UserResource(
            entity.getId(),
            entity.getEmail(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getRoles().stream()
                .map(role -> role.getName().name())
                .toList(),
            entity.getStatus().name(),
            entity.getPhoneNumber(),
            entity.getPreferredLanguage()
        );
    }
}

