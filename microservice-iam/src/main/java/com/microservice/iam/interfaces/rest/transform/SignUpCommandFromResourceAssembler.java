package com.microservice.iam.interfaces.rest.transform;

import com.microservice.iam.domain.model.commands.SignUpCommand;
import com.microservice.iam.domain.model.entities.Role;
import com.microservice.iam.interfaces.rest.resources.SignUpResource;

import java.util.ArrayList;

/**
 * Assembler to convert SignUpResource to SignUpCommand.
 */
public class SignUpCommandFromResourceAssembler {

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        var roles = resource.roles() != null
            ? resource.roles().stream().map(Role::toRoleFromName).toList()
            : new ArrayList<Role>();

        return new SignUpCommand(
            resource.firstName(),
            resource.lastName(),
            resource.email(),
            resource.password(),
            roles,
            resource.phoneNumber(),
            resource.preferredLanguage() != null ? resource.preferredLanguage() : "es"
        );
    }
}

