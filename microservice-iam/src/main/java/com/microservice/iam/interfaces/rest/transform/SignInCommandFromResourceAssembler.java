package com.microservice.iam.interfaces.rest.transform;

import com.microservice.iam.domain.model.commands.SignInCommand;
import com.microservice.iam.interfaces.rest.resources.SignInResource;

/**
 * Assembler to convert SignInResource to SignInCommand.
 */
public class SignInCommandFromResourceAssembler {

    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(
            resource.email(),
            resource.password()
        );
    }
}

