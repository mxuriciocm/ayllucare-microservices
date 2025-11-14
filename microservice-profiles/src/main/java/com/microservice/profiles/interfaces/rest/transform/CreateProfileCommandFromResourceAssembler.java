package com.microservice.profiles.interfaces.rest.transform;

import com.microservice.profiles.domain.model.commands.CreateProfileCommand;
import com.microservice.profiles.interfaces.rest.resources.CreateProfileResource;

public class CreateProfileCommandFromResourceAssembler {
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(resource.userId(), resource.firstName(), resource.lastName(), resource.phoneNumber());
    }
}
