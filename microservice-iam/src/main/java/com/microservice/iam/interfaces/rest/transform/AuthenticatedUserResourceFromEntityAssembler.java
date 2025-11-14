package com.microservice.iam.interfaces.rest.transform;

import com.microservice.iam.domain.model.aggregates.User;
import com.microservice.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * Assembler to convert User entity and token to AuthenticatedUserResource.
 */
public class AuthenticatedUserResourceFromEntityAssembler {

    public static AuthenticatedUserResource toResourceFromEntity(User entity, String token) {
        return new AuthenticatedUserResource(
            entity.getId(),
            entity.getEmail(),
            token
        );
    }
}

