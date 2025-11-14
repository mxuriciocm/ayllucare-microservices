package com.microservice.anamnesis.interfaces.rest.resources;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST resource for creating a new anamnesis session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnamnesisSessionResource {

    @Size(max = 500, message = "Initial reason must not exceed 500 characters")
    private String initialReason;
}

